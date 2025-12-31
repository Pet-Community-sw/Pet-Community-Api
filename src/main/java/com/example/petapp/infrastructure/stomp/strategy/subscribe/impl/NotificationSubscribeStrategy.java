package com.example.petapp.infrastructure.stomp.strategy.subscribe.impl;

import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.infrastructure.stomp.dto.SubscribeInfo;
import com.example.petapp.infrastructure.stomp.strategy.subscribe.BaseSubscribeTypeStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationSubscribeStrategy extends BaseSubscribeTypeStrategy {

    private static final String PATTEN = "/sub/notification/{id}";

    private final MemberQueryUseCase useCase;

    @Override
    public boolean isHandler(String destination) {
        return PATH.match(PATTEN, destination);
    }

    @Override
    public void handle(SubscribeInfo subscribeInfo) {
        Map<String, String> map = patternMap(PATTEN, subscribeInfo.getDestination());
        Long memberId = Long.valueOf(map.get("id"));
        useCase.findOrThrow(memberId);
        log.info("[STOMP] notification 구독 id : {}", memberId);
    }
}
