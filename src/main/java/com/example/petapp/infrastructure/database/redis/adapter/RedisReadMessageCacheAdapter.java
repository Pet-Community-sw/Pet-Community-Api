package com.example.petapp.infrastructure.database.redis.adapter;

import com.example.petapp.application.out.cache.ReadMessageCachePort;
import com.example.petapp.domain.chatmessage.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisReadMessageCacheAdapter implements ReadMessageCachePort {

    private final static String KEY_PREFIX = "chatRoomId:";
    private final static String KEY_SUFFIX = ":read";
    private final StringRedisTemplate redisTemplate;

    // Read Message per user
    public static String getKey(long chatRoomId) {
        return KEY_PREFIX + chatRoomId + KEY_SUFFIX;
    }

    @Override
    public void markAsRead(ChatMessage chatMessage) {
        redisTemplate.opsForHash().put(
                getKey(chatMessage.getChatRoomId()),
                String.valueOf(chatMessage.getSenderId()),
                String.valueOf(chatMessage.getSeq())
        );
    }

    @Override
    public Long findLastReadSeq(Long chatRoomId, Long memberId) {
        Object seq = redisTemplate.opsForHash().get(getKey(chatRoomId), String.valueOf(memberId));
        if (seq == null) {
            return 0L;
        }

        try {
            return Long.parseLong(seq.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    @Override
    public void deleteReadSeq(Long chatRoomId, Long memberId) {
        redisTemplate.opsForHash().delete(getKey(chatRoomId), String.valueOf(memberId));
    }

    @Override
    public void deleteRoomReadState(Long chatRoomId) {
        redisTemplate.delete(getKey(chatRoomId));
    }
}
