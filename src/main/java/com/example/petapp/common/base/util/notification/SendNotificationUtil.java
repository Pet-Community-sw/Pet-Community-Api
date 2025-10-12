package com.example.petapp.common.base.util.notification;

import com.example.petapp.domain.member.model.entity.Member;
import com.example.petapp.domain.sse.model.dto.NotificationListDto;
import com.example.petapp.infrastructure.redis.NotificationRedisPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendNotificationUtil {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> notificationRedisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final NotificationRedisPublisher notificationRedisPublisher;
//    private final FcmService fcmService;

    public void sendNotification(Member member, String message) {
        String key = "notifications:" + member.getId() + ":" + UUID.randomUUID();//알림 설정 최대 3일.
        NotificationListDto notificationListDto = new NotificationListDto(message, LocalDateTime.now());
        String json = null;
        try {
            json = objectMapper.writeValueAsString(notificationListDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("알림 보내는 도중 예외 발생", e);
        }
        notificationRedisTemplate.opsForValue().set(key, json, Duration.ofDays(3));
        if (Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember("foreGroundMembers:", member.getId().toString()))) {
            notificationRedisPublisher.publish("member:" + member.getId(), message);
        } else {
            log.info("backGroundMember");
//            fcmService.sendNotification(member.getFcmToken().getFcmToken(), "명냥로드", message);
        }
    }
}
