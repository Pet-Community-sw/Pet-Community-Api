package com.example.petapp.infrastructure.stomp.strategy.subscribe.impl;

import com.example.petapp.application.common.exception.ErrorCode;
import com.example.petapp.application.common.exception.PetCommunityException;
import com.example.petapp.application.usecase.chatroom.ChatRoomUseCase;
import com.example.petapp.infrastructure.stomp.dto.SubscribeInfo;
import com.example.petapp.infrastructure.stomp.store.ChatOnlineStore;
import com.example.petapp.infrastructure.stomp.store.ChatRoomSubscriptionStore;
import com.example.petapp.infrastructure.stomp.strategy.subscribe.SubscribeTypeStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatRoomSubscribeStrategy extends SubscribeTypeStrategy {

    private static final String KEY = "chatRoomId";
    private static final String PATTERN = "/sub/chat/{" + KEY + "}";

    private final ChatRoomUseCase chatRoomUseCase;
    private final ChatOnlineStore chatOnlineStore;
    private final ChatRoomSubscriptionStore chatRoomSubscriptionStore;

    @Override
    public boolean isHandler(String destination) {
        return PATH.match(PATTERN, destination);
    }

    @Override
    public void handle(SubscribeInfo subscribeInfo) {
        Map<String, String> map = pathMap(PATTERN, subscribeInfo.getDestination());
        Long chatRoomId = Long.valueOf(map.get(KEY));
        Long memberId = Long.valueOf(subscribeInfo.getPrincipal().getName());

        if (!chatRoomUseCase.isExist(chatRoomId, memberId)) {
            throw new PetCommunityException(ErrorCode.FORBIDDEN, "해당 채팅방에 접근할 권한이 없습니다.");
        }
        chatOnlineStore.createOnlineUser(chatRoomId, memberId);
        chatRoomSubscriptionStore.createChatRoomSubscription(subscribeInfo.getSubscriptionId(), chatRoomId);

    }
}
