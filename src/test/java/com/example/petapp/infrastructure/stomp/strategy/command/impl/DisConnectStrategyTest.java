package com.example.petapp.infrastructure.stomp.strategy.command.impl;

import com.example.petapp.application.out.cache.AppOnlineCachePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DisConnectStrategyTest {

    @Mock
    private AppOnlineCachePort appOnlineCachePort;

    @InjectMocks
    private DisConnectStrategy disConnectStrategy;

    @Test
    void principal이_없으면_온라인유저를_삭제하지_않는다() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.DISCONNECT);

        disConnectStrategy.handle(accessor);

        verify(appOnlineCachePort, never()).delete(org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void principal이_있으면_온라인유저를_삭제한다() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.DISCONNECT);
        accessor.setUser(() -> "10");

        disConnectStrategy.handle(accessor);

        verify(appOnlineCachePort).delete(10L);
    }
}
