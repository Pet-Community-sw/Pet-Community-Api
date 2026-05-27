package com.example.petapp.infrastructure.database.redis.adapter;

import com.example.petapp.application.out.cache.LastMessageCachePort;
import com.example.petapp.application.usecase.chatmessage.model.dto.LastMessageInfoDto;
import com.example.petapp.domain.chatmessage.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class RedisLastMessageCacheAdapter implements LastMessageCachePort {

    private final static String KEY_PREFIX = "chat:lastMessageInfo:";
    private final static String KEY_SEQ = "seq";
    private final static String KEY_LAST_MESSAGE = "lastMessage";
    private final static String KEY_LAST_MESSAGE_TIME = "lastMessageTime";

    private final StringRedisTemplate redisTemplate;

    // Last message info per chatRoom
    public static String key(long chatRoomId) {
        return KEY_PREFIX + chatRoomId;
    }

    @Override
    public void saveLastMessage(ChatMessage chatMessage) {
        Map<String, String> lastMessageInfo = new HashMap<>();
        lastMessageInfo.put(KEY_SEQ, String.valueOf(chatMessage.getSeq()));
        lastMessageInfo.put(KEY_LAST_MESSAGE, chatMessage.getMessage());
        lastMessageInfo.put(KEY_LAST_MESSAGE_TIME, String.valueOf(chatMessage.getMessageTime()));
        redisTemplate.opsForHash().putAll(key(chatMessage.getChatRoomId()), lastMessageInfo);
    }

    @Override
    public LastMessageInfoDto findLastMessageInfo(Long chatRoomId) {
        Map<Object, Object> lastMessageInfo = redisTemplate.opsForHash().entries(key(chatRoomId));
        String lastMessage = toStringOrEmpty(lastMessageInfo.get(KEY_LAST_MESSAGE));
        String lastMessageTime = toStringOrEmpty(lastMessageInfo.get(KEY_LAST_MESSAGE_TIME));
        Long lastSeq = toLongOrZero(lastMessageInfo.get(KEY_SEQ));
        return LastMessageInfoDto.builder()
                .lastSeq(lastSeq)
                .lastMessage(lastMessage)
                .lastMessageTime(lastMessageTime)
                .build();
    }

    @Override
    public void deleteLastMessageInfo(Long chatRoomId) {
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
