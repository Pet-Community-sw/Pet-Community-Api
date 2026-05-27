package com.example.petapp.application.usecase.chatmessage;

import com.example.petapp.application.usecase.chatmessage.model.type.CommandType;
import com.example.petapp.domain.chatmessage.model.ChatMessage;

public interface MessageTypeStrategy {

    void handle(ChatMessage chatMessage);

    CommandType getCommand();
}
