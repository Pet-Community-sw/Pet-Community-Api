package com.example.PetApp.common.stomp.strategy.impl.subscribeStrategy.impl;

import com.example.PetApp.common.stomp.SubscribeInfo;
import com.example.PetApp.common.stomp.strategy.impl.subscribeStrategy.BaseSubscribeTypeStrategy;
import com.example.PetApp.domain.groupchatroom.ChatRoomRepository;
import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.domain.query.QueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatRoomSubscribeTypeStrategy extends BaseSubscribeTypeStrategy {

    private static final String PATTERN = "/sub/chat/{chatRoomId}";

    private final QueryService queryService;
    private final ChatRoomRepository chatRoomRepository;
    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean isHandler(String destination) {
        return PATH.match(PATTERN, destination);
    }

    @Override
    public void handle(SubscribeInfo subscribeInfo) {
        Map<String, String> map = patternMap(PATTERN, subscribeInfo.getDestination());
        Long chatroomId = Long.valueOf(map.get("chatRoomId"));
        Long profileId = principalId(subscribeInfo);

        Profile profile = queryService.findByProfile(profileId);
        if (!chatRoomRepository.existsByIdAndUsersContains(chatroomId, profile.getId())) {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }

        redisTemplate.opsForSet().add("chatRoomId:" + chatroomId + ":onlineUsers", profileId.toString());

        log.info("[STOMP] 구독 chatroomId: {}, profileId: {}", chatroomId, profileId);
    }
}
