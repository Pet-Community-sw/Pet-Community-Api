package com.example.PetApp.common.stomp.strategy.command.impl.subscribeStrategy.impl;

import com.example.PetApp.common.stomp.SubscribeInfo;
import com.example.PetApp.common.stomp.strategy.command.impl.subscribeStrategy.BaseSubscribeTypeStrategy;
import com.example.PetApp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.PetApp.domain.query.QueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OneToOneSubscribeTypeStrategy extends BaseSubscribeTypeStrategy {

    private static final String PATTERN = "/user/chat/{chatRoomId}";

    private final QueryService queryService;
    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean isHandler(String destination) {
        return PATH.match(PATTERN, destination);
    }

    @Override
    public void handle(SubscribeInfo subscribeInfo) {
        Map<String, String> map = patternMap(PATTERN, subscribeInfo.getDestination());
        Long chatRoomId = Long.valueOf(map.get("chatRoomId"));
        Long memberId = principalId(subscribeInfo);

        ChatRoom chatRoom = queryService.findByChatRoom(chatRoomId);
        chatRoom.validateUser(memberId);

        redisTemplate.opsForSet().add("chatRoomId:" + chatRoomId + ":onlineMembers", memberId.toString());

        log.info("[STOMP] 구독 chatRoomId: {}, memberId: {}", chatRoomId, memberId);
    }
}
