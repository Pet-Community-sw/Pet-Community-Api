package com.example.petapp.application.in.chatting;

import com.example.petapp.application.in.chatting.model.type.CommandType;
import com.example.petapp.domain.chatting.model.ChatMessage;

public interface MessageTypeStrategy {

    void handle(ChatMessage chatMessage);

    CommandType getType();
}
