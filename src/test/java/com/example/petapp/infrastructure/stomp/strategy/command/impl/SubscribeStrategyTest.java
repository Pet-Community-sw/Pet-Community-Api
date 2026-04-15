package com.example.petapp.infrastructure.stomp.strategy.command.impl;

import com.example.petapp.infrastructure.stomp.dto.SubscribeInfo;
import com.example.petapp.infrastructure.stomp.strategy.subscribe.SubscribeTypeStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscribeStrategyTest {

    @Mock
    private SubscribeTypeStrategy handler1;
    @Mock
    private SubscribeTypeStrategy handler2;

    @InjectMocks
    private SubscribeStrategy subscribeStrategy;

    @Test
    void 매칭되는_핸들러가_있으면_해당핸들러를_호출한다() {
        SubscribeStrategy strategy = new SubscribeStrategy(List.of(handler1, handler2));
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination("/sub/chat/1");
        accessor.setUser(() -> "10");
        accessor.setSubscriptionId("sub-1");

        when(handler1.isHandler("/sub/chat/1")).thenReturn(false);
        when(handler2.isHandler("/sub/chat/1")).thenReturn(true);

        strategy.handle(accessor);

        verify(handler2).handle(any(SubscribeInfo.class));
        verify(handler1, never()).handle(any(SubscribeInfo.class));
    }

    @Test
    void destination또는_user가_없으면_예외가_발생한다() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setSubscriptionId("sub-1");

        assertThatThrownBy(() -> subscribeStrategy.handle(accessor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("destination 또는 user 정보가 없습니다.");
    }

    @Test
    void 매칭되는_핸들러가_없으면_예외가_발생한다() {
        SubscribeStrategy strategy = new SubscribeStrategy(List.of(handler1, handler2));
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination("/sub/unknown");
        accessor.setUser(() -> "10");
        accessor.setSubscriptionId("sub-1");

        when(handler1.isHandler("/sub/unknown")).thenReturn(false);
        when(handler2.isHandler("/sub/unknown")).thenReturn(false);

        assertThatThrownBy(() -> strategy.handle(accessor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("알 수 없는 구독 경로입니다.");
    }
}
