package com.example.petapp.application.in.chatting;

import com.example.petapp.application.in.chatting.model.dto.ChatMessageDto;

public interface ChattingUseCase {
    void sendToMessage(ChatMessageDto chatMessage, Long id);
}
