package com.example.petapp.application.usecase.chatmessage.service.strategy;

import com.example.petapp.application.out.cache.ReadMessageCachePort;
import com.example.petapp.application.usecase.chatmessage.MessageTypeStrategy;
import com.example.petapp.application.usecase.chatmessage.model.type.CommandType;
import com.example.petapp.domain.chatmessage.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReadStrategy implements MessageTypeStrategy {

    private final ReadMessageCachePort port;

    @Override
    public void handle(ChatMessage chatMessage) {
        port.markAsRead(chatMessage);
    }

    @Override
    public CommandType getCommand() {
        return CommandType.READ;
    }
}
