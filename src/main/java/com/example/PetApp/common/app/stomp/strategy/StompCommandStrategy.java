package com.example.PetApp.common.app.stomp.strategy;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

public interface StompCommandStrategy {
    void handle(StompHeaderAccessor accessor);
}
