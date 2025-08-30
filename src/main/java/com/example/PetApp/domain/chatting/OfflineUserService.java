package com.example.PetApp.domain.chatting;

import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import com.example.PetApp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.PetApp.domain.memberchatRoom.model.entity.MemberChatRoom;
import org.springframework.stereotype.Service;

@Service
public interface OfflineUserService {
    void setOfflineProfilesAndUnreadCount(ChatMessage chatMessage, ChatRoom chatRoom);

    void setOfflineMembersAndUnreadCount(ChatMessage chatMessage, MemberChatRoom memberChatRoom);
}
