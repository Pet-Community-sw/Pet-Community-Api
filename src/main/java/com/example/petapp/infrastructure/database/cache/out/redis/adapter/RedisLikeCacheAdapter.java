package com.example.petapp.infrastructure.database.cache.out.redis.adapter;

import com.example.petapp.application.out.cache.LikeCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisLikeCacheAdapter implements LikeCachePort {

    private final RedisTemplate<String, Long> likeRedisTemplate;

    public static String getKey(Long id) {
        return "post:likes:" + id;
    }

    @Override
    public Set<Long> get(Long id) {
        return likeRedisTemplate.opsForSet().members(getKey(id));
    }

    @Override
    public void create(Long id, Long value) {
        likeRedisTemplate.opsForSet().add(getKey(id), value);
    }

    @Override
    public void delete(Long id, Long value) {
        likeRedisTemplate.opsForSet().remove(getKey(id), value);
    }
}
