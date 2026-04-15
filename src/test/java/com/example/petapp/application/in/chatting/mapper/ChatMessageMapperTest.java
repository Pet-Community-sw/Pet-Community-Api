package com.example.petapp.application.in.chatting.mapper;

import com.example.petapp.application.in.chatting.model.dto.ChatMessageDto;
import com.example.petapp.application.in.chatting.model.dto.UserInfo;
import com.example.petapp.application.in.chatting.model.type.ChatRoomType;
import com.example.petapp.application.in.chatting.model.type.CommandType;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.chatting.model.ChatMessage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChatMessageMapperTest {

    @Test
    void dto를_ChatMessage로_변환한다() {
        ChatRoom chatRoom = mock(ChatRoom.class);
        when(chatRoom.getChatRoomType()).thenReturn(ChatRoomType.MANY);
        ChatMessageDto dto = new ChatMessageDto(CommandType.TALK, "client-1", 10L, "안녕하세요", 0);
        UserInfo userInfo = new UserInfo("몽이", "img.png");

        ChatMessage result = ChatMessageMapper.toEntity(dto, chatRoom, 1L, userInfo);

        assertThat(result.getChatRoomType()).isEqualTo(ChatRoomType.MANY);
        assertThat(result.getChatRoomId()).isEqualTo(10L);
        assertThat(result.getSenderId()).isEqualTo(1L);
        assertThat(result.getSenderName()).isEqualTo("몽이");
        assertThat(result.getSenderImageUrl()).isEqualTo("img.png");
        assertThat(result.getMessage()).isEqualTo("안녕하세요");
        assertThat(result.getClientMessageId()).isEqualTo("client-1");
        assertThat(result.getMessageTime()).isNotNull();
    }
}
