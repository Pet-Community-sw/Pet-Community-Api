package com.example.petapp.domain.chatting.strategy.impl;

import com.example.petapp.domain.chatting.AckInfoRepositoryImpl;
import com.example.petapp.domain.chatting.model.ChatMessage;
import com.example.petapp.domain.chatting.strategy.MessageTypeStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AckStrategy implements MessageTypeStrategy {

    private final AckInfoRepositoryImpl ackInfoRepositoryImpl;

    @Override
    public void handle(ChatMessage chatMessage) {
        ackInfoRepositoryImpl.deleteUser(chatMessage.getClientMessageId(), chatMessage.getSenderId());
    }
}
