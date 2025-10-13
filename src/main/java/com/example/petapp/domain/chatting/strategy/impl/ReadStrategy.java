package com.example.petapp.domain.chatting.strategy.impl;

import com.example.petapp.domain.chatting.model.entity.ChatMessage;
import com.example.petapp.domain.chatting.strategy.MessageTypeStrategy;
import com.example.petapp.port.InMemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReadStrategy implements MessageTypeStrategy {

    private final InMemoryService inMemoryService;

    @Override
    public void handle(ChatMessage chatMessage) {
        inMemoryService.createReadData(chatMessage);
    }
}
