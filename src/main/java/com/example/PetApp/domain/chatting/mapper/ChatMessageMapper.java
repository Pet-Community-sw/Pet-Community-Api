package com.example.PetApp.domain.chatting.mapper;

import com.example.PetApp.domain.chatting.model.dto.ChatMessageDto;
import com.example.PetApp.domain.chatting.model.dto.UserInfo;
import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import com.example.PetApp.domain.groupchatroom.model.dto.request.UpdateChatRoomList;
import com.example.PetApp.domain.groupchatroom.model.entity.ChatRoom;

import java.time.LocalDateTime;
import java.util.Map;

public class ChatMessageMapper {

    public static ChatMessage toEntity(ChatMessageDto chatMessageDto, ChatRoom chatRoom, Long senderId, UserInfo userInfo) {
        return ChatMessage.builder()
                .messageType(chatMessageDto.getMessageType())
                .chatRoomType(chatRoom.getChatRoomType())
                .chatRoomId(chatMessageDto.getChatRoomId())
                .senderId(senderId)
                .senderName(userInfo.getUserName())
                .senderImageUrl(userInfo.getImageUrl())
                .message(chatMessageDto.getMessage())
                .messageTime(LocalDateTime.now())
                .build();
    }

    public static UpdateChatRoomList toUpdateChatRoomList(Long roomId, ChatMessage message, Map<Long, Long> unReadMap) {
        return UpdateChatRoomList.builder()
                .chatRoomId(roomId)
                .lastMessage(message.getMessage())
                .lastMessageTime(message.getMessageTime())
                .unReadCount(unReadMap)
                .build();
    }

}
