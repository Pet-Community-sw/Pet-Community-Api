package com.example.petapp.infrastructure.stomp.strategy.impl.subscribeStrategy.impl;

import com.example.petapp.domain.query.QueryService;
import com.example.petapp.infrastructure.stomp.SubscribeInfo;
import com.example.petapp.infrastructure.stomp.strategy.impl.subscribeStrategy.BaseSubscribeTypeStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoomListSubscribeStrategy extends BaseSubscribeTypeStrategy {

    private static final String PATTERN = "/sub/list";

    private final QueryService queryService;

    @Override
    public boolean isHandler(String destination) {
        return PATH.match(PATTERN, destination);
    }

    @Override
    public void handle(SubscribeInfo subscribeInfo) {
        Long userId = principalId(subscribeInfo);
        queryService.findByProfile(userId);
    }
}
