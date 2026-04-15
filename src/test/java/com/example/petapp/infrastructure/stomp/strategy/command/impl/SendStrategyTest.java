package com.example.petapp.infrastructure.stomp.strategy.command.impl;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SendStrategyTest {

    private final SendStrategy sendStrategy = new SendStrategy();

    @Test
    void destination이_없으면_예외가_발생한다() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
        accessor.setUser(() -> "1");

        assertThatThrownBy(() -> sendStrategy.handle(accessor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("destination 또는 user 정보가 없습니다.");
    }

    @Test
    void user가_없으면_예외가_발생한다() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
        accessor.setDestination("/pub/chat");

        assertThatThrownBy(() -> sendStrategy.handle(accessor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("destination 또는 user 정보가 없습니다.");
    }

    @Test
    void destination과_user가_있으면_정상_처리한다() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
        accessor.setDestination("/pub/chat");
        accessor.setUser(() -> "1");

        assertThatNoException().isThrownBy(() -> sendStrategy.handle(accessor));
    }
}
