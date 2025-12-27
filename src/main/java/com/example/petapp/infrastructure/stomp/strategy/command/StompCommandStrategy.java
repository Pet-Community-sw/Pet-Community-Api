package com.example.petapp.infrastructure.stomp.strategy.command;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

public interface StompCommandStrategy {
    void handle(StompHeaderAccessor accessor);
}
