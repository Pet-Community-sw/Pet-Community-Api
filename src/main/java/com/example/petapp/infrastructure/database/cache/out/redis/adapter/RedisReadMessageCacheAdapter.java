package com.example.petapp.infrastructure.database.cache.out.redis.adapter;

import com.example.petapp.application.out.cache.ReadMessageCachePort;
import com.example.petapp.domain.chatting.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisReadMessageCacheAdapter implements ReadMessageCachePort {

    private final StringRedisTemplate redisTemplate;

    // Read Message per user
    public static String getKey(long chatRoomId) {
        return "chatRoomId:" + chatRoomId + ":read";
    }

    @Override
    public void create(ChatMessage chatMessage) {
        redisTemplate.opsForHash().put(
                getKey(chatMessage.getChatRoomId()),
                String.valueOf(chatMessage.getSenderId()),
                String.valueOf(chatMessage.getSeq())
        );
    }

    @Override
    public Long find(Long chatRoomId, Long userId) {
        Object seq = redisTemplate.opsForHash().get(getKey(chatRoomId), String.valueOf(userId));
        return seq == null ? 0 : (Long) seq;
    }

    @Override
    public void delete(Long chatRoomId, Long userId) {
        redisTemplate.opsForHash().delete(getKey(chatRoomId), String.valueOf(userId));
    }

    @Override
    public void delete(Long chatRoomId) {
        redisTemplate.delete(getKey(chatRoomId));
    }
}
