package com.example.PetApp.domain.chatting.strategy.impl;

import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import com.example.PetApp.domain.chatting.strategy.MessageTypeStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReadStrategy implements MessageTypeStrategy {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void handle(ChatMessage chatMessage) {
        redisTemplate.opsForHash().put("chatRoomId:" + chatMessage.getChatRoomId() + ":read",
                String.valueOf(chatMessage.getSenderId()),
                String.valueOf(chatMessage.getSeq()));
    }
}
