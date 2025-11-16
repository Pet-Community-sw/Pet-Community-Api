package com.example.petapp.domain.chatting.offline;

import com.example.petapp.domain.chatting.model.ChatMessage;
import com.example.petapp.domain.groupchatroom.model.entity.ChatRoom;
import org.springframework.stereotype.Service;

@Service
public interface OfflineUserService {
    void setOfflineUsersAndUnreadCount(ChatMessage chatMessage, ChatRoom chatRoom);
}
