package com.example.petapp.domain.chatting.strategy;

import com.example.petapp.domain.chatting.model.entity.ChatMessage;

public interface MessageTypeStrategy {

    void handle(ChatMessage chatMessage);
}
