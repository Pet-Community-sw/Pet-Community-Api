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

    // Online users in chat room
    public static String getKey(long chatRoomId) {
        return "chatRoomId:" + chatRoomId + ":onlineUsers";
    }

    @Override
    public void create(Long chatRoomId, Long profileId) {
        redisTemplate.opsForSet().add(getKey(chatRoomId), profileId.toString());
    }

    @Override
    public void delete(Long chatRoomId, Long profileId) {
        redisTemplate.opsForSet().remove(getKey(chatRoomId), profileId.toString());
    }

    @Override
    public Set<String> find(Long id) {
        return redisTemplate.opsForSet().members(getKey(id));
    }
}
