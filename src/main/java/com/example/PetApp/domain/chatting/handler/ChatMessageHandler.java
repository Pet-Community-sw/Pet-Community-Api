package com.example.PetApp.domain.chatting.handler;

import com.example.PetApp.domain.chatting.model.dto.ChatMessageDto;
import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import org.springframework.stereotype.Service;

@Service
public interface ChatMessageHandler {
    void handleEnterMessage(ChatMessage chatMessage);

    void handleLeaveMessage(ChatMessage chatMessage, Long senderId);

    void handleTalkMessage(ChatMessage chatMessage);

    void handleReadMessage(ChatMessageDto chatMessageDto, Long senderId);
}
