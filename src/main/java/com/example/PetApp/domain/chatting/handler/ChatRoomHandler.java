package com.example.PetApp.domain.chatting.handler;

import com.example.PetApp.domain.chatting.model.dto.ChatMessageDto;
import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import org.springframework.stereotype.Service;

@Service
public interface ChatRoomHandler {
    ChatMessage handleGroupChat(ChatMessageDto chatMessageDto, Long senderId);

    ChatMessage handleOneToOneChat(ChatMessageDto chatMessageDto, Long senderId);

}
