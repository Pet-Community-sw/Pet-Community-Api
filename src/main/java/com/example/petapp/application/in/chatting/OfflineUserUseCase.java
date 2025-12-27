package com.example.petapp.application.in.chatting;

import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.chatting.model.ChatMessage;

public interface OfflineUserUseCase {
    void setOfflineUsersAndUnreadCount(ChatMessage chatMessage, ChatRoom chatRoom);
}
