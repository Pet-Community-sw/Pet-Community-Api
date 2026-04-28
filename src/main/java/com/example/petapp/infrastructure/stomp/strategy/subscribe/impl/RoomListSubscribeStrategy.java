package com.example.petapp.infrastructure.stomp.strategy.subscribe.impl;

import com.example.petapp.application.in.profile.ProfileUseCase;
import com.example.petapp.infrastructure.stomp.dto.SubscribeInfo;
import com.example.petapp.infrastructure.stomp.strategy.subscribe.SubscribeTypeStrategy;
import com.example.petapp.interfaces.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoomListSubscribeStrategy extends SubscribeTypeStrategy {

    private static final String PATTERN = "/sub/list/{userId}";

    private final ProfileUseCase useCase;

    @Override
    public boolean isHandler(String destination) {
        return PATH.match(PATTERN, destination);
    }

    @Override
    public void handle(SubscribeInfo subscribeInfo) {
        Map<String, String> map = pathMap(PATTERN, subscribeInfo.getDestination());
        Long userId = Long.valueOf(map.get("userId"));
        if (userId.equals(Long.valueOf(subscribeInfo.getPrincipal().getName()))) {
            useCase.findOrThrow(userId);
        } else {
            throw new ForbiddenException("[STOMP] userId가 다릅니다.");
        }
    }
}
