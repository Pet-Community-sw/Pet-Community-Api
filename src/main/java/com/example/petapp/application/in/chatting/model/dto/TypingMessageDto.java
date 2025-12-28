package com.example.petapp.application.in.chatting.model.dto;

public record TypingMessageDto(
        Long roomId,
        boolean isTyping
) {
}
