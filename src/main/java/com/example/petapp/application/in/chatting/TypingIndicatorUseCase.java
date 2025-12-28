package com.example.petapp.application.in.chatting;

import com.example.petapp.application.in.chatting.model.dto.TypingMessageDto;

public interface TypingIndicatorUseCase {
    void sendTypingStatus(TypingMessageDto typingMessageDto, Long id);
}
