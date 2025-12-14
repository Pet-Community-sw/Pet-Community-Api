package com.example.petapp.infrastructure.database.cache.out.redis.adapter;

import com.example.petapp.application.in.notification.dto.NotificationListDto;
import com.example.petapp.application.out.cache.NotificationsCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RedisNotificationCacheAdapter implements NotificationsCachePort {

    private final RedisTemplate<String, NotificationListDto> notificationRedisTemplate;

    public static String getKey(Long id) {
        return "notification:" + id;
    }

    @Override
    public void create(Long id, NotificationListDto notificationListDto, int day) {
        notificationRedisTemplate.opsForList().rightPush(getKey(id), notificationListDto);
        notificationRedisTemplate.expire(getKey(id), Duration.ofDays(day));

    }

    @Override
    public List<NotificationListDto> getList(Long id) {
        return notificationRedisTemplate.opsForList().range(getKey(id), 0, -1);
    }

}
