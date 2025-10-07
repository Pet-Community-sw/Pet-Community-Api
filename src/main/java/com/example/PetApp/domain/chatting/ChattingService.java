package com.example.PetApp.domain.chatting;

import com.example.PetApp.domain.chatting.model.dto.ChatMessageDto;
import org.springframework.stereotype.Service;

@Service
public interface ChattingService {
    void sendToMessage(ChatMessageDto chatMessage, Long id);
}
