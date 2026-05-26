package com.example.petapp.application.usecase.chatting.model.dto;

public record TypingMessageDto(
        Long roomId,
        boolean isTyping
) {
}
