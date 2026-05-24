package com.example.petapp.infrastructure.database.cache.out.redis;

import com.example.petapp.application.usecase.chatting.model.dto.SendResponseDto;
import lombok.Getter;

@Getter
public class RedisNotificationMessage {
    String destination;
    SendResponseDto<?> payload;
}
