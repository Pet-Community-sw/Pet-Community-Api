package com.example.petapp.infrastructure.database.cache.out.redis.adapter;

import com.example.petapp.infrastructure.stomp.DestinationCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisDestinationCachePort implements DestinationCachePort {

    private final StringRedisTemplate template;

    @Override
    public Set<String> getSet(String key) {
        return template.opsForSet().members(key);
    }

    @Override
    public void delete(String key) {
        template.delete(key);
    }

    @Override
    public void create(String key, String value) {
        template.opsForSet().add(key, value);
        //key가 없으면 set을 생성해서 넣음 key가 있으면 set에 value추가
    }
}
