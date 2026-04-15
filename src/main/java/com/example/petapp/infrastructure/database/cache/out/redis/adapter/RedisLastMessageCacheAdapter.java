package com.example.petapp.infrastructure.database.cache.out.redis.adapter;

import com.example.petapp.application.in.chatting.model.dto.LastMessageInfoDto;
import com.example.petapp.application.out.cache.LastMessageCachePort;
import com.example.petapp.domain.chatting.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class RedisLastMessageCacheAdapter implements LastMessageCachePort {

    private final StringRedisTemplate redisTemplate;

    // Last message info per chatRoom
    public static String key(long chatRoomId) {
        return "chat:lastMessageInfo:" + chatRoomId;
    }

    @Override
    public void create(ChatMessage chatMessage) {
        Map<String, String> lastMessageInfo = new HashMap<>();
        lastMessageInfo.put("seq", String.valueOf(chatMessage.getSeq()));
        lastMessageInfo.put("lastMessage", chatMessage.getMessage());
        lastMessageInfo.put("lastMessageTime", String.valueOf(chatMessage.getMessageTime()));
        redisTemplate.opsForHash().putAll(key(chatMessage.getChatRoomId()), lastMessageInfo);
    }

    @Override
    public LastMessageInfoDto find(Long id) {
        Map<Object, Object> lastMessageInfo = redisTemplate.opsForHash().entries(key(id));
        String lastMessage = toStringOrEmpty(lastMessageInfo.get("lastMessage"));
        String lastMessageTime = toStringOrEmpty(lastMessageInfo.get("lastMessageTime"));
        Long lastSeq = toLongOrZero(lastMessageInfo.get("seq"));
        return LastMessageInfoDto.builder()
                .lastSeq(lastSeq)
                .lastMessage(lastMessage)
                .lastMessageTime(lastMessageTime)
                .build();
    }

    @Override
    public void delete(Long chatRoomId) {
        redisTemplate.delete(key(chatRoomId));
    }

    private String toStringOrEmpty(Object value) {
        return value == null ? "" : value.toString();
    }

    private Long toLongOrZero(Object value) {
        if (value == null) {
            return 0L;
        }

        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
