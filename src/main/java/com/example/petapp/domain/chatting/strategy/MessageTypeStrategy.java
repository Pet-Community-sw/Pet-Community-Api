package com.example.petapp.domain.chatting.strategy;

import com.example.petapp.domain.chatting.model.ChatMessage;

public interface MessageTypeStrategy {

    void handle(ChatMessage chatMessage);
}
