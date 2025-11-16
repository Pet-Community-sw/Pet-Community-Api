package com.example.petapp.domain.chatting.strategy.impl;

import com.example.petapp.domain.chatting.AckInfoRepository;
import com.example.petapp.domain.chatting.model.ChatMessage;
import com.example.petapp.domain.chatting.strategy.MessageTypeStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AckStrategy implements MessageTypeStrategy {

    private final AckInfoRepository ackInfoRepository;

    @Override
    public void handle(ChatMessage chatMessage) {
        ackInfoRepository.deleteUser(chatMessage.getClientMessageId(), chatMessage.getSenderId());
    }
}
