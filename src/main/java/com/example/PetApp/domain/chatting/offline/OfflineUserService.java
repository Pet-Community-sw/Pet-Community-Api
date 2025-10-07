package com.example.PetApp.domain.chatting.offline;

import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import com.example.PetApp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.PetApp.domain.memberchatRoom.model.entity.MemberChatRoom;
import org.springframework.stereotype.Service;

@Service
public interface OfflineUserService {
    void setOfflineUsersAndUnreadCount(ChatMessage chatMessage, ChatRoom chatRoom);

    void setOfflineUsersAndUnreadCount(ChatMessage chatMessage, MemberChatRoom memberChatRoom);
}
