package com.example.petapp.infrastructure.redis;

import com.example.petapp.domain.sse.SseEmitterManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationRedisSubscriber {
    private final SseEmitterManager sseEmitterManager;

    public void onMessage(String channel, String message) {
        log.info("notification channel:{}, message:{}", channel, message);

        Long memberId = Long.valueOf(channel.split(":")[1]);
        sseEmitterManager.sendNotification(memberId, message);
    }
}
