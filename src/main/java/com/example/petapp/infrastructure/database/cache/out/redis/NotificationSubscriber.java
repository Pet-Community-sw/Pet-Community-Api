package com.example.petapp.infrastructure.database.cache.out.redis;

import com.example.petapp.application.common.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class NotificationSubscriber implements MessageListener {

    private final SimpMessagingTemplate template;
    private final JsonUtil jsonUtil;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String body = new String(message.getBody(), StandardCharsets.UTF_8);

        RedisNotificationMessage redisMessage =
                jsonUtil.fromJson(body, RedisNotificationMessage.class);

        template.convertAndSend(
                redisMessage.getDestination(),
                redisMessage.getPayload()
        );
    }
}