package com.example.petapp.application.usecase.chatting;

import com.example.petapp.application.usecase.chatting.model.type.CommandType;
import com.example.petapp.domain.chatting.model.ChatMessage;

public interface MessageTypeStrategy {

    void handle(ChatMessage chatMessage);

    CommandType getCommand();
}
