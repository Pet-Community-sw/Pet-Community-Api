package com.example.petapp.domain.chatting.strategy.impl;

import com.example.petapp.application.out.cache.ReadMessageCachePort;
import com.example.petapp.domain.chatting.model.ChatMessage;
import com.example.petapp.domain.chatting.strategy.MessageTypeStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReadStrategy implements MessageTypeStrategy {

    private final ReadMessageCachePort port;

    @Override
    public void handle(ChatMessage chatMessage) {
        port.create(chatMessage);
    }
}
