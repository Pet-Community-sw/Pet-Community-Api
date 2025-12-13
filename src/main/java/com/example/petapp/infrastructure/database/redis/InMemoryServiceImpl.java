package com.example.petapp.infrastructure.database.redis;

import com.example.petapp.port.InMemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InMemoryServiceImpl implements InMemoryService {

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean existRoomSeq(Long chatRoomId) {
        return redisTemplate.hasKey(RedisKeys.seqByRoomId(chatRoomId));
    }

    @Override
    public Long incrementSeq(Long chatRoomId) {
        return redisTemplate.opsForValue().increment(RedisKeys.seqByRoomId(chatRoomId));
    }

    @Override
    public void createRoomSeq(Long chatRoomId, Long seq) {
        redisTemplate.opsForValue().setIfAbsent(RedisKeys.seqByRoomId(chatRoomId), String.valueOf(seq));
    }

    @Override
    public void deleteRoomSeq(Long chatRoomId) {
        redisTemplate.delete(RedisKeys.seqByRoomId(chatRoomId));
    }
}
