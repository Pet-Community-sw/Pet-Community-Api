package com.example.petapp.infrastructure.stomp.strategy;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

public interface StompCommandStrategy {
    void handle(StompHeaderAccessor accessor);
}
