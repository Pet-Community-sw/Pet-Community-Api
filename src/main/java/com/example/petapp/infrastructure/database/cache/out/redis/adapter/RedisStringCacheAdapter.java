package com.example.petapp.infrastructure.database.cache.out.redis.adapter;

import com.example.petapp.application.out.cache.StringCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisStringCacheAdapter implements StringCachePort {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void create(String key, String memberId) {
        redisTemplate.opsForSet().add(key, memberId);
    }


    @Override
    public Set<String> find(String key) {
        return redisTemplate.opsForSet().members(key);
    }
}
