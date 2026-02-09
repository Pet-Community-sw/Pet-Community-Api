package com.example.petapp.application.service.chatting.strategy;

import com.example.petapp.application.in.chatting.MessageTypeStrategy;
import com.example.petapp.application.in.chatting.model.type.CommandType;
import com.example.petapp.domain.chatting.model.ChatMessage;
import com.example.petapp.infrastructure.database.adapter.AckInfoRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AckStrategy implements MessageTypeStrategy {

    private final AckInfoRepositoryAdapter ackInfoRepositoryAdapter;

    @Override
    public void handle(ChatMessage chatMessage) {
        ackInfoRepositoryAdapter.deleteUser(chatMessage.getClientMessageId(), chatMessage.getSenderId());
    }

    @Override
    public CommandType getCommand() {
        return CommandType.ACK;
    }
}
