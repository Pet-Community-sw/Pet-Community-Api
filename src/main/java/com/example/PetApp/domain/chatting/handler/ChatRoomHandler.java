package com.example.PetApp.domain.chatting.handler;

import com.example.PetApp.domain.chatting.model.dto.ChatMessageDto;
import org.springframework.stereotype.Service;

@Service
public interface ChatRoomHandler {
    void handleGroupChat(ChatMessageDto chatMessageDto, Long senderId);

    void handleOneToOneChat(ChatMessageDto chatMessageDto, Long senderId);

}
