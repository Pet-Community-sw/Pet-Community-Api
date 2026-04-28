package com.example.petapp.infrastructure.stomp.strategy.subscribe.impl;

import com.example.petapp.application.in.chatroom.ChatRoomUseCase;
import com.example.petapp.application.out.cache.ChatOnlineCachePort;
import com.example.petapp.infrastructure.stomp.DestinationCachePort;
import com.example.petapp.infrastructure.stomp.dto.SubscribeInfo;
import com.example.petapp.infrastructure.stomp.strategy.subscribe.SubscribeTypeStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatRoomSubscribeStrategy extends SubscribeTypeStrategy {

    private static final String PATTERN = "/sub/chat/{chatRoomId}";

    private final ChatRoomUseCase chatRoomUseCase;
    private final ChatOnlineCachePort chatOnlineCachePort;
    private final DestinationCachePort destinationCachePort;

    @Override
    public boolean isHandler(String destination) {
        return PATH.match(PATTERN, destination);
    }

    @Override
    public void handle(SubscribeInfo subscribeInfo) {
        Map<String, String> map = pathMap(PATTERN, subscribeInfo.getDestination());
        String chatRoomId = map.get("chatRoomId");
        String profileId = subscribeInfo.getPrincipal().getName();

        if (!chatRoomUseCase.isExist(Long.valueOf(chatRoomId), Long.valueOf(profileId))) {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }
        chatOnlineCachePort.create(chatRoomId, profileId);
        destinationCachePort.create(subscribeInfo.getSubscriptionId(), chatRoomId);

        log.info("[STOMP] 구독 chatRoomId: {}, profileId: {}", chatRoomId, profileId);
    }
}
