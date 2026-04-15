package com.example.petapp.application.service.chatting;

import com.example.petapp.application.in.chatroom.ChatRoomQueryUseCase;
import com.example.petapp.application.in.chatting.MessageTypeStrategy;
import com.example.petapp.application.in.chatting.OfflineUserUseCase;
import com.example.petapp.application.in.chatting.model.dto.ChatMessageDto;
import com.example.petapp.application.in.chatting.model.type.ChatRoomType;
import com.example.petapp.application.in.chatting.model.type.CommandType;
import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.application.in.profile.ProfileQueryUseCase;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.chatting.model.ChatMessage;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.model.Profile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChattingServiceTest {

    @Mock
    private ProfileQueryUseCase profileQueryUseCase;
    @Mock
    private ChatRoomQueryUseCase chatRoomQueryUseCase;
    @Mock
    private MemberQueryUseCase memberQueryUseCase;
    @Mock
    private OfflineUserUseCase offlineUserUseCase;
    @Mock
    private MessageTypeStrategy talkStrategy;

    @Test
    void 다중채팅방에서는_프로필정보로_메시지를_생성한다() {
        Map<CommandType, MessageTypeStrategy> map = new EnumMap<>(CommandType.class);
        map.put(CommandType.TALK, talkStrategy);
        ChattingService chattingService = new ChattingService(
                profileQueryUseCase,
                map,
                chatRoomQueryUseCase,
                memberQueryUseCase,
                offlineUserUseCase
        );

        ChatMessageDto dto = new ChatMessageDto(CommandType.TALK, "c1", 10L, "안녕", 0);
        ChatRoom chatRoom = org.mockito.Mockito.mock(ChatRoom.class);
        Profile profile = org.mockito.Mockito.mock(Profile.class);
        when(chatRoomQueryUseCase.find(10L)).thenReturn(chatRoom);
        when(chatRoom.getChatRoomType()).thenReturn(ChatRoomType.MANY);
        when(profileQueryUseCase.findOrThrow(1L)).thenReturn(profile);
        when(profile.getPetName()).thenReturn("몽이");
        when(profile.getPetImageUrl()).thenReturn("dog.png");

        chattingService.sendToMessage(dto, 1L);

        verify(chatRoom).validateUser(1L);
        ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(talkStrategy).handle(captor.capture());
        ChatMessage savedMessage = captor.getValue();
        assertThat(savedMessage.getSenderName()).isEqualTo("몽이");
        assertThat(savedMessage.getSenderImageUrl()).isEqualTo("dog.png");
        assertThat(savedMessage.getChatRoomId()).isEqualTo(10L);
        assertThat(savedMessage.getMessage()).isEqualTo("안녕");
        verify(offlineUserUseCase).setOfflineUsersAndUnreadCount(savedMessage, chatRoom);
        verify(memberQueryUseCase, never()).findOrThrow(1L);
    }

    @Test
    void 일대일채팅방에서는_회원정보로_메시지를_생성한다() {
        Map<CommandType, MessageTypeStrategy> map = new EnumMap<>(CommandType.class);
        map.put(CommandType.TALK, talkStrategy);
        ChattingService chattingService = new ChattingService(
                profileQueryUseCase,
                map,
                chatRoomQueryUseCase,
                memberQueryUseCase,
                offlineUserUseCase
        );

        ChatMessageDto dto = new ChatMessageDto(CommandType.TALK, "c2", 20L, "hello", 0);
        ChatRoom chatRoom = org.mockito.Mockito.mock(ChatRoom.class);
        Member member = org.mockito.Mockito.mock(Member.class);
        when(chatRoomQueryUseCase.find(20L)).thenReturn(chatRoom);
        when(chatRoom.getChatRoomType()).thenReturn(ChatRoomType.ONE);
        when(memberQueryUseCase.findOrThrow(2L)).thenReturn(member);
        when(member.getName()).thenReturn("철수");
        when(member.getMemberImageUrl()).thenReturn("member.png");

        chattingService.sendToMessage(dto, 2L);

        ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(talkStrategy).handle(captor.capture());
        ChatMessage savedMessage = captor.getValue();
        assertThat(savedMessage.getSenderName()).isEqualTo("철수");
        assertThat(savedMessage.getSenderImageUrl()).isEqualTo("member.png");
        verify(profileQueryUseCase, never()).findOrThrow(2L);
    }

    @Test
    void 등록되지_않은_커맨드는_예외를_발생시킨다() {
        ChattingService chattingService = new ChattingService(
                profileQueryUseCase,
                Map.of(),
                chatRoomQueryUseCase,
                memberQueryUseCase,
                offlineUserUseCase
        );

        ChatMessageDto dto = new ChatMessageDto(CommandType.TALK, "c3", 30L, "x", 0);
        ChatRoom chatRoom = org.mockito.Mockito.mock(ChatRoom.class);
        Profile profile = org.mockito.Mockito.mock(Profile.class);
        when(chatRoomQueryUseCase.find(30L)).thenReturn(chatRoom);
        when(chatRoom.getChatRoomType()).thenReturn(ChatRoomType.MANY);
        when(profileQueryUseCase.findOrThrow(3L)).thenReturn(profile);
        when(profile.getPetName()).thenReturn("뽀삐");
        when(profile.getPetImageUrl()).thenReturn("p.png");

        assertThatThrownBy(() -> chattingService.sendToMessage(dto, 3L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] : messageType 외 요청");
    }
}
