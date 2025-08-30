package com.example.PetApp.domain.chatting.mapper;

import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import com.example.PetApp.domain.groupchatroom.model.dto.request.UpdateChatRoomList;

import java.util.Map;

public class ChatMessageMapper {

    public static UpdateChatRoomList toUpdateChatRoomList(Long roomId, ChatMessage message, Map<Long, Long> unReadMap) {
        return UpdateChatRoomList.builder()
                .chatRoomId(roomId)
                .lastMessage(message.getMessage())
                .lastMessageTime(message.getMessageTime())
                .unReadCount(unReadMap)
                .build();
    }
}
