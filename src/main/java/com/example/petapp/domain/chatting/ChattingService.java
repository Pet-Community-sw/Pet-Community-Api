package com.example.petapp.domain.chatting;

import com.example.petapp.domain.chatting.model.dto.ChatMessageDto;
import org.springframework.stereotype.Service;

@Service
public interface ChattingService {
    void sendToMessage(ChatMessageDto chatMessage, Long id);
}
