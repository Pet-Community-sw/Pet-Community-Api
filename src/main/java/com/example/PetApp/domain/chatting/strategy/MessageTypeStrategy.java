package com.example.PetApp.domain.chatting.strategy;

import com.example.PetApp.domain.chatting.model.entity.ChatMessage;

public interface MessageTypeStrategy {

    void handle(ChatMessage chatMessage);
}
