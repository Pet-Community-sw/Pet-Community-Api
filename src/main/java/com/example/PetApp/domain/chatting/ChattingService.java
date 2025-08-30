package com.example.PetApp.domain.chatting;

import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import org.springframework.stereotype.Service;

@Service
public interface ChattingService {
    void sendToMessage(ChatMessage chatMessage, Long id);
}
