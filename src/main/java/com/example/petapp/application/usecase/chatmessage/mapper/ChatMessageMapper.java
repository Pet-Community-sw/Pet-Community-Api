package com.example.petapp.application.usecase.chatmessage.mapper;

import com.example.petapp.application.usecase.chatmessage.model.dto.ChatMessageDto;
import com.example.petapp.application.usecase.chatmessage.model.dto.UserInfo;
import com.example.petapp.domain.chatmessage.model.ChatMessage;
import com.example.petapp.domain.chatroom.model.ChatRoom;

import java.time.LocalDateTime;

public class ChatMessageMapper {

    public static ChatMessage toEntity(ChatMessageDto chatMessageDto, ChatRoom chatRoom, Long senderId, UserInfo userInfo) {
        return ChatMessage.builder()
                .chatRoomId(chatMessageDto.getChatRoomId())
                .senderId(senderId)
                .senderName(userInfo.getUserName())
                .senderImageUrl(userInfo.getImageUrl())
                .message(chatMessageDto.getMessage())
                .clientMessageId(chatMessageDto.getClientMessageId())
                .messageTime(LocalDateTime.now())
                .build();
    }
}
