package com.example.petapp.infrastructure.stomp.strategy.command.impl;

import com.example.petapp.infrastructure.stomp.store.ChatOnlineStore;
import com.example.petapp.infrastructure.stomp.store.ChatRoomSubscriptionStore;
import com.example.petapp.infrastructure.stomp.strategy.command.StompCommandStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnSubscribeStrategy implements StompCommandStrategy {

    private final ChatRoomSubscriptionStore chatRoomSubscriptionStore;
    private final ChatOnlineStore chatOnlineStore;

    @Override
    public void handle(StompHeaderAccessor accessor) {
        String subscriptionId = accessor.getSubscriptionId();
        Long memberId = Long.valueOf(accessor.getUser().getName());

        chatRoomSubscriptionStore.getChatRoomIdBySubscriptionId(subscriptionId)
                .ifPresent(chatRoomId -> {
                    chatOnlineStore.deleteOnlineUser(chatRoomId, memberId);
                    chatRoomSubscriptionStore.deleteChatRoomSubscription(subscriptionId);
                });
    }

    @Override
    public StompCommand getCommand() {
        return StompCommand.UNSUBSCRIBE;
    }
}
