package com.example.petapp.infrastructure.database.redis;

import com.example.petapp.domain.chatting.model.ChatMessage;
import com.example.petapp.domain.chatting.model.dto.LastMessageInfoDto;
import com.example.petapp.port.InMemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InMemoryServiceImpl implements InMemoryService {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void createLastMessageInfoData(ChatMessage chatMessage) {
        Map<String, String> lastMessageInfo = new HashMap<>();
        lastMessageInfo.put("seq", String.valueOf(chatMessage.getSeq()));
        lastMessageInfo.put("lastMessage", chatMessage.getMessage());
        lastMessageInfo.put("lastMessageTime", String.valueOf(chatMessage.getMessageTime()));
        redisTemplate.opsForHash().putAll(RedisKeys.lastMessageInfo(chatMessage.getChatRoomId()), lastMessageInfo);
    }

    @Override
    public LastMessageInfoDto getLastMessageInfoData(Long id) {
        Map<Object, Object> lastMessageInfo = redisTemplate.opsForHash().entries(RedisKeys.lastMessageInfo(id));
        String lastMessage = (String) lastMessageInfo.getOrDefault("lastMessage", "");
        String lastMessageTime = (String) lastMessageInfo.getOrDefault("lastMessageTime", "");
        Long lastSeq = (Long) lastMessageInfo.getOrDefault("seq", 0);
        return LastMessageInfoDto.builder()
                .lastSeq(lastSeq)
                .lastMessage(lastMessage)
                .lastMessageTime(lastMessageTime)
                .build();
    }

    @Override
    public void deleteLastMessageInfoData(Long chatRoomId) {
        redisTemplate.delete(RedisKeys.lastMessageInfo(chatRoomId));
    }
    //-------------------------------------------------------------------------------------

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
