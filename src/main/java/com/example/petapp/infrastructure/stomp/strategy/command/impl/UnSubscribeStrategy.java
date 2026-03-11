package com.example.petapp.infrastructure.stomp.strategy.command.impl;

import com.example.petapp.application.out.cache.ChatOnlineCachePort;
import com.example.petapp.infrastructure.stomp.DestinationCachePort;
import com.example.petapp.infrastructure.stomp.strategy.command.StompCommandStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnSubscribeStrategy implements StompCommandStrategy {

    private final DestinationCachePort destinationCachePort;
    private final ChatOnlineCachePort chatOnlineCachePort;

    @Override
    public void handle(StompHeaderAccessor accessor) {
        String id = accessor.getSubscriptionId();
        String profileId = accessor.getUser().getName();

        String chatRoomId = destinationCachePort.get(id);
        chatOnlineCachePort.delete(chatRoomId, profileId);
        destinationCachePort.delete(id);
    }

    @Override
    public StompCommand getCommand() {
        return StompCommand.UNSUBSCRIBE;
    }
}
