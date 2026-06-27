package com.example.petapp.infrastructure.stomp.strategy.subscribe.impl;

import com.example.petapp.application.usecase.chatroom.ChatRoomUseCase;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.infrastructure.stomp.dto.SubscribeInfo;
import com.example.petapp.infrastructure.stomp.store.ChatOnlineStore;
import com.example.petapp.infrastructure.stomp.store.ChatRoomSubscriptionStore;
import com.example.petapp.infrastructure.stomp.strategy.subscribe.SubscribeTypeStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ChatRoomSubscribeStrategy extends SubscribeTypeStrategy {

    private static final String KEY = "chatRoomId";
    private static final String PATTERN = "/sub/chat/{" + KEY + "}";

    private final ChatRoomUseCase chatRoomUseCase;
    private final ChatOnlineStore chatOnlineStore;
    private final ChatRoomSubscriptionStore chatRoomSubscriptionStore;

    @Override
    public boolean supports(String destination) {
        return PATH.match(PATTERN, destination);
    }

    @Override
    public void handle(SubscribeInfo subscribeInfo) {
        Map<String, String> pathVariables = extractPathVariables(PATTERN, subscribeInfo.getDestination());
        Long chatRoomId = Long.valueOf(pathVariables.get(KEY));
        Long memberId = Long.valueOf(subscribeInfo.getPrincipal().getName());

        ChatRoom chatRoom = chatRoomUseCase.find(chatRoomId);
        chatRoom.validateUser(memberId);

        chatOnlineStore.addOnlineUser(chatRoomId, memberId);
        chatRoomSubscriptionStore.createChatRoomSubscription(subscribeInfo.getSubscriptionId(), chatRoomId);

    }
}


