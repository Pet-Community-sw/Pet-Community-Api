package com.example.petapp.infrastructure.stomp.strategy.subscribe.impl;

import com.example.petapp.application.common.exception.ErrorCode;
import com.example.petapp.application.common.exception.PetCommunityException;
import com.example.petapp.application.usecase.member.MemberQueryUseCase;
import com.example.petapp.infrastructure.stomp.dto.SubscribeInfo;
import com.example.petapp.infrastructure.stomp.strategy.subscribe.SubscribeTypeStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoomListSubscribeStrategy extends SubscribeTypeStrategy {

    private static final String KEY = "userId";
    private static final String PATTERN = "/sub/list/{" + KEY + "}";

    private final MemberQueryUseCase useCase;

    @Override
    public boolean isHandler(String destination) {
        return PATH.match(PATTERN, destination);
    }

    @Override
    public void handle(SubscribeInfo subscribeInfo) {
        Map<String, String> map = pathMap(PATTERN, subscribeInfo.getDestination());
        Long userId = Long.valueOf(map.get(KEY));
        if (userId.equals(Long.valueOf(subscribeInfo.getPrincipal().getName()))) {
            useCase.findOrThrow(userId);
        } else {
            throw new PetCommunityException(ErrorCode.FORBIDDEN, "[STOMP] userId가 다릅니다.");
        }
    }
}
