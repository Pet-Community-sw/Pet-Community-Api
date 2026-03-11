package com.example.petapp.infrastructure.database.cache.out.redis.adapter;

import com.example.petapp.application.out.cache.ChatOnlineCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisChatOnlineCacheAdapter implements ChatOnlineCachePort {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void create(String chatRoomId, String profileId) {
        redisTemplate.opsForSet().add(getKey(chatRoomId), profileId);
    }

    @Override
    public void delete(String chatRoomId, String profileId) {
        redisTemplate.opsForSet().remove(getKey(chatRoomId), profileId);
    }

    @Override
    public Set<String> find(Long id) {
        return redisTemplate.opsForSet().members(getKey(String.valueOf(id)));
    }

    private String getKey(String chatRoomId) {
        return "chatRoomId:" + chatRoomId + ":onlineUsers";
    }
}
