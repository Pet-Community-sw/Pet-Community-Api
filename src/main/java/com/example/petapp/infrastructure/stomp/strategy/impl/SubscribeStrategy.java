package com.example.petapp.infrastructure.stomp.strategy.impl;

import com.example.petapp.infrastructure.stomp.DestinationCachePort;
import com.example.petapp.infrastructure.stomp.SubscribeInfo;
import com.example.petapp.infrastructure.stomp.strategy.StompCommandStrategy;
import com.example.petapp.infrastructure.stomp.strategy.impl.subscribeStrategy.SubscribeTypeStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscribeStrategy implements StompCommandStrategy {

    private final List<SubscribeTypeStrategy> handlers;  //스프링이 구현체 다 넣어줌 ㅋㅋㅋㅋㅋ
    private final DestinationCachePort port;

    @Override
    public void handle(StompHeaderAccessor accessor) {
        log.info("[STOMP] subscribe 전략 시작");

        String destination = accessor.getDestination();
        Principal user = accessor.getUser();

        if (destination == null || user == null) {
            throw new IllegalArgumentException("destination 또는 user 정보가 없습니다.");
        }

        port.create(accessor.getSubscriptionId(), destination);

        SubscribeInfo subscribeInfo = SubscribeInfo.builder()
                .destination(destination)
                .principal(user)
                .build();

        for (SubscribeTypeStrategy subscribeTypeStrategy : handlers) {
            if (subscribeTypeStrategy.isHandler(destination)) {
                subscribeTypeStrategy.handle(subscribeInfo);
                return;
            }
        }
        throw new IllegalArgumentException("알 수 없는 구독 경로입니다.");
    }
}
