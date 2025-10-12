package com.example.petapp.infrastructure.stomp.strategy.impl.subscribeStrategy.impl;

import com.example.petapp.domain.query.QueryService;
import com.example.petapp.infrastructure.stomp.SubscribeInfo;
import com.example.petapp.infrastructure.stomp.strategy.impl.subscribeStrategy.BaseSubscribeTypeStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoomListSubscribeStrategy extends BaseSubscribeTypeStrategy {

    private static final String PATTERN = "/sub/list/{userId}";

    private final QueryService queryService;

    @Override
    public boolean isHandler(String destination) {
        return PATH.match(PATTERN, destination);
    }

    @Override
    public void handle(SubscribeInfo subscribeInfo) {
        Map<String, String> map = patternMap(PATTERN, subscribeInfo.getDestination());
        Long userId = Long.valueOf(map.get("userId"));

        //todo : 방에 0개이면 연결 x getList먼저하고 그담에 구독하는게 좋을듯
        queryService.findByProfile(userId);
    }
}
