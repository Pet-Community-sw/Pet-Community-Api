package com.example.petapp.infrastructure.database.cache.out.redis.adapter;

import com.example.petapp.application.out.cache.SeqCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisSeqCacheAdapter implements SeqCachePort {

    private final StringRedisTemplate redisTemplate;

    // Room Seq
    private static String key(Long roomId) {
        return "chatRoom:" + roomId + ":seq";
    }

    @Override
    public boolean exist(Long chatRoomId) {
        return redisTemplate.hasKey(key(chatRoomId));
    }

    @Override
    public Long increment(Long chatRoomId) {
        return redisTemplate.opsForValue().increment(key(chatRoomId));
    }

    @Override
    public void create(Long chatRoomId, Long seq) {
        redisTemplate.opsForValue().setIfAbsent(key(chatRoomId), String.valueOf(seq));
    }

    @Override
    public void delete(Long chatRoomId) {
        redisTemplate.delete(key(chatRoomId));
    }
}
