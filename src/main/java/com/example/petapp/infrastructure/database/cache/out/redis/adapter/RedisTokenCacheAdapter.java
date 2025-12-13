package com.example.petapp.infrastructure.database.cache.out.redis.adapter;

import com.example.petapp.application.out.cache.TokenCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisTokenCacheAdapter implements TokenCachePort {

    private final StringRedisTemplate template;

    @Override
    public void create(String key, String value, long duration) {
        template.opsForValue().set(key, value, Duration.ofSeconds(duration));
    }

    @Override
    public boolean exist(String key) {
        return template.hasKey(key);
    }

}
