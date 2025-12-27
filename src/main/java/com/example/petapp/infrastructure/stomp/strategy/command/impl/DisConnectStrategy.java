package com.example.petapp.infrastructure.stomp.strategy.command.impl;

import com.example.petapp.application.out.cache.AppOnlineCachePort;
import com.example.petapp.infrastructure.stomp.strategy.command.StompCommandStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DisConnectStrategy implements StompCommandStrategy {

    private final AppOnlineCachePort appOnlineCachePort;

    @Override
    public void handle(StompHeaderAccessor accessor) {
        log.info("[STOMP][DISCONNECT] 전략 시작");

        appOnlineCachePort.delete(Long.valueOf(accessor.getUser().getName()));
        log.info("[STOMP][DISCONNECT] 온라인 유저 삭제 ");
    }
}
