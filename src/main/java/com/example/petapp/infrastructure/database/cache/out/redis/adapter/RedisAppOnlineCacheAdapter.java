package com.example.petapp.infrastructure.database.cache.out.redis.adapter;

import com.example.petapp.application.out.cache.AppOnlineCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisAppOnlineCacheAdapter implements AppOnlineCachePort {

    private final StringRedisTemplate redisTemplate;

    // Foreground members
    public static String key() {
        return "foreGroundMembers";
    }

    @Override
    public void create(Long id) {
        redisTemplate.opsForSet().add(key(), id.toString());
    }

    @Override
    public void delete(Long id) {
        redisTemplate.opsForSet().remove(key(), id.toString());
    }

    //todo : websocket connect할 때 유저 저장하면 될 것 같은데...
    @Override
    public Boolean exist(Long id) {
        return redisTemplate.opsForSet().isMember(key(), id.toString());
    }

}
