package com.example.petapp.infrastructure.database.cache.out.redis.adapter;

import com.example.petapp.infrastructure.stomp.DestinationCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisDestinationCachePort implements DestinationCachePort {

    private final StringRedisTemplate template;

    @Override
    public void delete(String key) {
        template.delete(getKey(key));
    }

    @Override
    public void create(String key, String value) {
        template.opsForValue().set(getKey(key), value);
    }

    @Override
    public String get(String key) {
        return template.opsForValue().get(getKey(key));
    }

    private String getKey(String subscriptionId) {
        return "stomp:subscriptionId:" + subscriptionId;
    }
}
