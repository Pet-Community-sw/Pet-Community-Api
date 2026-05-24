package com.example.petapp.infrastructure.database.cache.out.redis;

import com.example.petapp.application.common.JsonUtil;
import com.example.petapp.application.out.SendPort;
import com.example.petapp.application.usecase.chatting.model.dto.SendResponseDto;
import com.example.petapp.application.usecase.chatting.model.type.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationSubscriber implements MessageListener {

    private final SendPort port;
    private final JsonUtil jsonUtil;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String body = new String(message.getBody(), StandardCharsets.UTF_8);

        NotificationTestDto notificationTestDto = jsonUtil.fromJson(body, NotificationTestDto.class);

//        RedisNotificationMessage redisMessage =
//                jsonUtil.fromJson(body, RedisNotificationMessage.class);

        log.info("Received message: {}", notificationTestDto);

        port.send(notificationTestDto.getDestination(),
                SendResponseDto.builder().commandType(CommandType.NOTIFICATION).body(notificationTestDto.getNotificationDto()).build());
    }
}