package com.example.petapp.infrastructure.database.cache.out.redis.adapter;

import com.example.petapp.application.out.cache.EmailCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisEmailCacheAdapter implements EmailCachePort {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void createWithDuration(String key, String value, long duration) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(duration));
    }

    @Override
    public boolean exist(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
