package com.example.petapp.application.usecase.chatmessage.service.strategy;

import com.example.petapp.application.out.SendPort;
import com.example.petapp.application.out.cache.LastMessageCachePort;
import com.example.petapp.application.out.cache.SeqCachePort;
import com.example.petapp.application.usecase.chatroom.ChatRoomUseCase;
import com.example.petapp.application.usecase.profile.ProfileUseCase;
import com.example.petapp.domain.chatmessage.ChatMessageRepository;
import com.example.petapp.domain.chatmessage.model.ChatMessage;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.infrastructure.stomp.store.ChatOnlineStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
    private ChatOnlineStore chatOnlineStore;
    @Mock
    private LastMessageCachePort lastMessageCachePort;
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
                .messageTime(LocalDateTime.now())
                .build();

        ChatRoom chatRoom = org.mockito.Mockito.mock(ChatRoom.class);
        when(chatRoom.getUsers()).thenReturn(Set.of(1L));
        when(chatRoomUseCase.find(10L)).thenReturn(chatRoom);
        when(seqCachePort.exists(10L)).thenReturn(false);
        when(chatMessageRepository.findCurrent(10L)).thenReturn(Optional.empty());
        when(seqCachePort.incrementAndGet(10L)).thenReturn(1L);
        when(chatOnlineStore.getOnlineUserList(10L)).thenReturn(Set.of());

        talkStrategy.handle(chatMessage);

        assertThat(chatMessage.getSeq()).isEqualTo(1L);
        verify(seqCachePort).initializeIfAbsent(10L, 0L);
        verify(chatMessageRepository).save(chatMessage);
        verify(sendPort).send(eq("/sub/chat/10"), any());
        verify(lastMessageCachePort).saveLastMessage(chatMessage);
        verify(eventPublisher, never()).publishEvent(any());
    }
}
