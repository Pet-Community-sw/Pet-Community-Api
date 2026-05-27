package com.example.petapp.infrastructure.database.redis;

import com.example.petapp.application.usecase.chatmessage.model.dto.NotificationDto;
import lombok.Getter;

@Getter
public class RedisNotificationMessage {

    String destination;
    NotificationDto notificationDto;
}
