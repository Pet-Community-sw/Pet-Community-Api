package com.example.petapp.infrastructure.stomp.strategy.command.impl;

import com.example.petapp.application.out.cache.ChatOnlineCachePort;
import com.example.petapp.infrastructure.stomp.DestinationCachePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnSubscribeStrategyTest {

    @Mock
    private DestinationCachePort destinationCachePort;
    @Mock
    private ChatOnlineCachePort chatOnlineCachePort;

    @InjectMocks
    private UnSubscribeStrategy unSubscribeStrategy;

    @Test
    void 구독해제시_온라인유저와_목적지캐시를_정리한다() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.UNSUBSCRIBE);
        accessor.setSubscriptionId("sub-1");
        accessor.setUser(() -> "2");
        when(destinationCachePort.get("sub-1")).thenReturn("100");

        unSubscribeStrategy.handle(accessor);

        verify(chatOnlineCachePort).delete("100", "2");
        verify(destinationCachePort).delete("sub-1");
    }
}
