package com.example.petapp.infrastructure.stomp.interceptor;

import com.example.petapp.infrastructure.stomp.strategy.StompCommandStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.util.Map;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class StompInterceptor implements ChannelInterceptor {

    private final Map<StompCommand, StompCommandStrategy> commandStrategyMap;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            log.warn("[STOMP] accessor is null");
            return message;
        }
        log.info("[STOMP] command : {}, destination : {}", accessor.getCommand(), accessor.getDestination());
        StompCommandStrategy strategy = commandStrategyMap.get(accessor.getCommand());
        if (strategy != null) {
            strategy.handle(accessor);
        } else {
            log.info("[STOMP] command 외 요청");
        }
        return message;
    }
}
