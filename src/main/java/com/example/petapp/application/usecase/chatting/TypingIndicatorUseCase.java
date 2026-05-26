package com.example.petapp.application.usecase.chatting;

import com.example.petapp.application.usecase.chatting.model.dto.TypingMessageDto;

public interface TypingIndicatorUseCase {
    void sendTypingStatus(TypingMessageDto typingMessageDto, Long id);
}
