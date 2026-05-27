package com.example.petapp.application.usecase.chatmessage.model.dto;

public record TypingMessageDto(
        Long roomId,
        boolean isTyping
) {
}
