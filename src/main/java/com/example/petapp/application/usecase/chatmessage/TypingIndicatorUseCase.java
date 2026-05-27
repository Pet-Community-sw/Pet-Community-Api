package com.example.petapp.application.usecase.chatmessage;

import com.example.petapp.application.usecase.chatmessage.model.dto.TypingMessageDto;

public interface TypingIndicatorUseCase {
    void sendTypingStatus(TypingMessageDto typingMessageDto, Long id);
}
