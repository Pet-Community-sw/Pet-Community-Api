package com.example.petapp.application.usecase.chatting;

import com.example.petapp.application.usecase.chatting.model.dto.ChatMessageDto;

public interface ChattingUseCase {
    void sendToMessage(ChatMessageDto chatMessage, Long id);
}
