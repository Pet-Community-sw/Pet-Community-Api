package com.example.petapp.application.service.chatting.strategy;

import com.example.petapp.application.in.chatroom.ChatRoomUseCase;
import com.example.petapp.application.in.profile.ProfileUseCase;
import com.example.petapp.application.out.SendPort;
import com.example.petapp.application.out.cache.ChatOnlineCachePort;
import com.example.petapp.application.out.cache.LastMessageCachePort;
import com.example.petapp.application.out.cache.ReadMessageCachePort;
import com.example.petapp.application.out.cache.SeqCachePort;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.chatting.AckInfoRepository;
import com.example.petapp.domain.chatting.ChatMessageRepository;
import com.example.petapp.domain.chatting.model.ChatMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.TaskScheduler;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TalkStrategyTest {

    @Mock
    private ChatRoomUseCase chatRoomUseCase;
    @Mock
    private ProfileUseCase profileUseCase;
    @Mock
    private SendPort sendPort;
    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private SeqCachePort seqCachePort;
    @Mock
    private ChatOnlineCachePort chatOnlineCachePort;
    @Mock
    private ReadMessageCachePort readMessageCachePort;
    @Mock
    private LastMessageCachePort lastMessageCachePort;
    @Mock
    private AckInfoRepository ackInfoRepository;
    @Mock
    private SimpUserRegistry simpUserRegistry;
    @Mock
    private TaskScheduler resendScheduler;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TalkStrategy talkStrategy;

    @Test
    void seq캐시가_없으면_초기화후_메시지를_저장전송한다() {
        ChatMessage chatMessage = ChatMessage.builder()
                .clientMessageId("c1")
                .chatRoomId(10L)
                .senderId(1L)
                .senderName("몽이")
                .message("안녕")
                .users(Set.of())
                .messageTime(LocalDateTime.now())
                .build();

        ChatRoom chatRoom = org.mockito.Mockito.mock(ChatRoom.class);
        when(chatRoom.getUsers()).thenReturn(Set.of(1L));
        when(chatRoomUseCase.find(10L)).thenReturn(chatRoom);
        when(seqCachePort.exist(10L)).thenReturn(false);
        when(chatMessageRepository.findCurrent(10L)).thenReturn(Optional.empty());
        when(seqCachePort.increment(10L)).thenReturn(1L);
        when(chatOnlineCachePort.find(10L)).thenReturn(Set.of());
        when(ackInfoRepository.find("c1")).thenReturn(Set.of());
        when(resendScheduler.schedule(any(Runnable.class), any(java.util.Date.class)))
                .thenAnswer(invocation -> {
                    Runnable runnable = invocation.getArgument(0);
                    runnable.run();
                    return null;
                });

        talkStrategy.handle(chatMessage);

        assertThat(chatMessage.getSeq()).isEqualTo(1L);
        verify(seqCachePort).create(10L, 0L);
        verify(chatMessageRepository).save(chatMessage);
        verify(sendPort).send(eq("/sub/chat/10"), any());
        verify(ackInfoRepository).save("c1", Set.of(1L));
        verify(lastMessageCachePort).create(chatMessage);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void 재전송시_미확인유저가_온라인이면_sendToUser를_호출하고_ack를_정리한다() {
        ChatMessage chatMessage = ChatMessage.builder()
                .clientMessageId("c2")
                .chatRoomId(20L)
                .senderId(2L)
                .senderName("철수")
                .message("재전송 테스트")
                .users(Set.of())
                .messageTime(LocalDateTime.now())
                .build();

        ChatRoom chatRoom = org.mockito.Mockito.mock(ChatRoom.class);
        SimpUser simpUser = org.mockito.Mockito.mock(SimpUser.class);
        when(chatRoom.getUsers()).thenReturn(Set.of(2L));
        when(chatRoomUseCase.find(20L)).thenReturn(chatRoom);
        when(seqCachePort.exist(20L)).thenReturn(true);
        when(seqCachePort.increment(20L)).thenReturn(5L);
        when(chatOnlineCachePort.find(20L)).thenReturn(Set.of());
        when(ackInfoRepository.find("c2")).thenReturn(Set.of(2L));
        when(simpUserRegistry.getUser("2")).thenReturn(simpUser);
        when(resendScheduler.schedule(any(Runnable.class), any(java.util.Date.class)))
                .thenAnswer(invocation -> {
                    Runnable runnable = invocation.getArgument(0);
                    runnable.run();
                    return null;
                });

        talkStrategy.handle(chatMessage);

        verify(sendPort).sendToUser(eq("2"), eq("/sub/chat"), any());
        verify(ackInfoRepository).clear("c2");
    }
}
