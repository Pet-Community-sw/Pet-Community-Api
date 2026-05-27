package com.example.petapp.infrastructure.database.redis.adapter;

import com.example.petapp.application.out.cache.EmailCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisEmailCacheAdapter implements EmailCachePort {

    private final StringRedisTemplate template;

    @Override
    public void createAuthCode(String email, String code, long ttlSeconds) {
        template.opsForValue().set(email, code, Duration.ofSeconds(ttlSeconds));
    }

    @Override
    public boolean exists(String email) {
        return template.hasKey(email);
    }

    @Override
    public void deleteAuthCode(String email) {
        template.delete(email);
    }

    @Override
    public String findAuthCode(String email) {
        return template.opsForValue().get(email);
    }
}
