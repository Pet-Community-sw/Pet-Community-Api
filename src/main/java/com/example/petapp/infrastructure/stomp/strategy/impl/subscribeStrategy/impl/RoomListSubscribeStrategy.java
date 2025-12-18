package com.example.petapp.infrastructure.stomp.strategy.impl.subscribeStrategy.impl;

import com.example.petapp.application.in.profile.ProfileQueryUseCase;
import com.example.petapp.infrastructure.stomp.SubscribeInfo;
import com.example.petapp.infrastructure.stomp.strategy.impl.subscribeStrategy.BaseSubscribeTypeStrategy;
import com.example.petapp.interfaces.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoomListSubscribeStrategy extends BaseSubscribeTypeStrategy {

    private static final String PATTERN = "/sub/list/{userId}";

    private final ProfileQueryUseCase useCase;

    @Override
    public boolean isHandler(String destination) {
        return PATH.match(PATTERN, destination);
    }

    @Override
    public void handle(SubscribeInfo subscribeInfo) {
        Map<String, String> map = patternMap(PATTERN, subscribeInfo.getDestination());
        Long userId = Long.valueOf(map.get("userId"));
        Long principalId = principalId(subscribeInfo);
        if (userId.equals(principalId)) {
            useCase.findOrThrow(userId);
        } else {
            throw new ForbiddenException("[STOMP] userId가 다릅니다.");
        }
    }
}
