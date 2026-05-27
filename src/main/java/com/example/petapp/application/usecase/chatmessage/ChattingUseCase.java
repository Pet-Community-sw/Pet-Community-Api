package com.example.petapp.application.usecase.chatmessage;

import com.example.petapp.application.usecase.chatmessage.model.dto.ChatMessageDto;

public interface ChattingUseCase {
    void sendToMessage(ChatMessageDto chatMessage, Long id);
}
