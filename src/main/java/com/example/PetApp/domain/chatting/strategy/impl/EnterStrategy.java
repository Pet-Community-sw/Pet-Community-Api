package com.example.PetApp.domain.chatting.strategy.impl;

import com.example.PetApp.config.redis.RedisPublisher;
import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import com.example.PetApp.domain.chatting.strategy.MessageTypeStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnterStrategy implements MessageTypeStrategy {

    private final RedisPublisher redisPublisher;

    @Override
    public void handle(ChatMessage chatMessage) {
        chatMessage.setMessage(chatMessage.getSenderName() + "님이 입장하셨습니다.");
        redisPublisher.publish(chatMessage);
    }
}
