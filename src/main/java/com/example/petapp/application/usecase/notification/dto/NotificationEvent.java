package com.example.petapp.application.usecase.notification.dto;

public record NotificationEvent(
        Long id,
        String message
) {
}
