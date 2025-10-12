package com.example.petapp.domain.chatting.mapper;

import com.example.petapp.domain.chatting.model.dto.ChatMessageDto;
import com.example.petapp.domain.chatting.model.dto.UserInfo;
import com.example.petapp.domain.chatting.model.entity.ChatMessage;
import com.example.petapp.domain.groupchatroom.model.entity.ChatRoom;

import java.time.LocalDateTime;

public class ChatMessageMapper {

    public static ChatMessage toEntity(ChatMessageDto chatMessageDto, ChatRoom chatRoom, Long senderId, UserInfo userInfo) {
        return ChatMessage.builder()
                .chatRoomType(chatRoom.getChatRoomType())
                .chatRoomId(chatMessageDto.getChatRoomId())
                .senderId(senderId)
                .senderName(userInfo.getUserName())
                .senderImageUrl(userInfo.getImageUrl())
                .message(chatMessageDto.getMessage())
                .messageTime(LocalDateTime.now())
                .build();
    }
}
