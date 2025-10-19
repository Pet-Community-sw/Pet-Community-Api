package com.example.petapp.infrastructure.stomp.strategy.impl;

import com.example.petapp.infrastructure.stomp.strategy.StompCommandStrategy;
import com.example.petapp.port.InMemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UnSubscribeStrategy implements StompCommandStrategy {

    private final InMemoryService inMemoryService;

    @Override
    public void handle(StompHeaderAccessor accessor) {
        /*
         *1. redis로 사용자 구독 경로 추적
         *2. SimpRegistry로 user의 구독 경로 추적
         *3. subscriptionId를 chat-123처럼 id받아서 처리
         * 급하니까 서버에서 처리하는걸로
         * todo : 테스트해봐야함.
         * */

        String userId = accessor.getUser().getName();
        Set<String> subscribePaths = inMemoryService.getStringSetData(userId);
        for (String path : subscribePaths) {
            if (path.startsWith("/sub/chat")) {
                inMemoryService.deleteOnlineDate(Long.valueOf(path.substring("/sub/chat/".length())), Long.valueOf(userId));
            }
        }
    }
}
