package com.example.petapp.infrastructure.database.cache.out.redis.adapter;

import com.example.petapp.application.out.cache.TokenCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisTokenCacheAdapter implements TokenCachePort {

    private static final String KEY = "blacklist:";
    private final StringRedisTemplate template;

    @Override
    public void blacklist(String accessToken, long duration) {
        template.opsForValue().set(KEY + accessToken, "logout", Duration.ofSeconds(duration));
    }

    @Override
    public boolean isBlacklisted(String accessToken) {
        return template.hasKey(KEY + accessToken);
    }

}
