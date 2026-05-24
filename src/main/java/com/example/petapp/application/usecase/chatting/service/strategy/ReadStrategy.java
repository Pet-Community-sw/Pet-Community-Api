package com.example.petapp.application.usecase.chatting.service.strategy;

import com.example.petapp.application.out.cache.ReadMessageCachePort;
import com.example.petapp.application.usecase.chatting.MessageTypeStrategy;
import com.example.petapp.application.usecase.chatting.model.type.CommandType;
import com.example.petapp.domain.chatting.model.ChatMessage;
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

    @Override
    public CommandType getCommand() {
        return CommandType.READ;
    }
}
