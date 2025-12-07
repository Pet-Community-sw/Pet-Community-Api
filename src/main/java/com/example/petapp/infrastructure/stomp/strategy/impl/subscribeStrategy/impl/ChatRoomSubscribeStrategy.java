package com.example.petapp.infrastructure.stomp.strategy.impl.subscribeStrategy.impl;

import com.example.petapp.application.in.profile.ProfileQueryUseCase;
import com.example.petapp.domain.chatroom.ChatRoomRepository;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.infrastructure.stomp.SubscribeInfo;
import com.example.petapp.infrastructure.stomp.strategy.impl.subscribeStrategy.BaseSubscribeTypeStrategy;
import com.example.petapp.port.InMemoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatRoomSubscribeStrategy extends BaseSubscribeTypeStrategy {

    private static final String PATTERN = "/sub/chat/{chatRoomId}";

    private final ProfileQueryUseCase useCase;
    private final ChatRoomRepository chatRoomRepository;
    private final InMemoryService inMemoryService;

    @Override
    public boolean isHandler(String destination) {
        return PATH.match(PATTERN, destination);
    }

    @Override
    public void handle(SubscribeInfo subscribeInfo) {
        Map<String, String> map = patternMap(PATTERN, subscribeInfo.getDestination());
        Long chatroomId = Long.valueOf(map.get("chatRoomId"));
        Long profileId = principalId(subscribeInfo);
        Profile profile = useCase.findOrThrow(profileId);
        if (!chatRoomRepository.existAndContain(chatroomId, profile.getId())) {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }
        inMemoryService.createOnlineData(chatroomId, profileId);
        log.info("[STOMP] 구독 chatroomId: {}, profileId: {}", chatroomId, profileId);
    }
}
