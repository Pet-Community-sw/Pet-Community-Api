package com.example.petapp.infrastructure.database.redis.adapter;

import com.example.petapp.application.out.cache.SeqCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisSeqCacheAdapter implements SeqCachePort {

    private final static String KEY_PREFIX = "chatRoom:";
    private final static String KEY_SUFFIX = ":seq";
    private final StringRedisTemplate redisTemplate;

    // Room Seq
    private static String key(Long roomId) {
        return KEY_PREFIX + roomId + KEY_SUFFIX;
    }

    @Override
    public boolean exists(Long chatRoomId) {
        return redisTemplate.hasKey(key(chatRoomId));
    }

    @Override
    public Long incrementAndGet(Long chatRoomId) {
        return redisTemplate.opsForValue().increment(key(chatRoomId));
    }

    @Override
    public void initializeIfAbsent(Long chatRoomId, Long seq) {
        redisTemplate.opsForValue().setIfAbsent(key(chatRoomId), String.valueOf(seq));
    }

    @Override
    public void deleteSeq(Long chatRoomId) {
        redisTemplate.delete(key(chatRoomId));
    }
}
