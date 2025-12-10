package com.example.petapp.infrastructure.database.cache.out.redis.adapter;

import com.example.petapp.application.out.cache.NotificationsCachePort;
import com.example.petapp.domain.notification.model.dto.NotificationListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisNotificationCacheAdapter implements NotificationsCachePort {

    private final RedisTemplate<String, NotificationListDto> notificationRedisTemplate;

    public static String key(long memberId) {
        return "notification:" + memberId;
    }

    @Override
    public void create(Long memberId, NotificationListDto notificationListDto, int day) {
        notificationRedisTemplate.opsForList().rightPush(key(memberId), notificationListDto);
        notificationRedisTemplate.expire(key(memberId), Duration.ofDays(day));

    }

}
