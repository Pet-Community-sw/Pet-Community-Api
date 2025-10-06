package com.example.PetApp.common.stomp.strategy.command.impl;

import com.example.PetApp.common.stomp.strategy.command.StompCommandStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendStrategy implements StompCommandStrategy {



    @Override
    public void handle(StompHeaderAccessor accessor) {

    }
}
