package com.example.petapp.infrastructure.database.cache.out.redis;

import com.example.petapp.application.usecase.chatting.model.dto.NotificationDto;
import lombok.Getter;

@Getter
public class NotificationTestDto {

    String destination;
    NotificationDto notificationDto;
}
