package com.example.petapp.infrastructure.stomp.strategy.subscribe.impl;

import com.example.petapp.application.usecase.member.MemberUseCase;
import com.example.petapp.infrastructure.stomp.dto.SubscribeInfo;
import com.example.petapp.infrastructure.stomp.strategy.subscribe.SubscribeTypeStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationSubscribeStrategy extends SubscribeTypeStrategy {

    private static final String KEY = "id";
    private static final String PATTERN = "/sub/notification/{" + KEY + "}";

    private final MemberUseCase useCase;

    @Override
    public boolean isHandler(String destination) {
        return PATH.match(PATTERN, destination);
    }

    @Override
    public void handle(SubscribeInfo subscribeInfo) {
        Map<String, String> map = pathMap(PATTERN, subscribeInfo.getDestination());
        Long memberId = Long.valueOf(map.get(KEY));
        useCase.findOrThrow(memberId);
        log.info("[STOMP] notification 구독 id : {}", memberId);
    }
}
