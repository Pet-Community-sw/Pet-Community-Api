package com.example.PetApp.config.redis;

import com.example.PetApp.domain.chatting.ChatMessageRepository;
import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import com.example.PetApp.domain.chatting.model.type.ChatRoomType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    private final ChatMessageRepository chatMessageRepository;

    public void publish(ChatMessage chatMessage) {
        Long seq = stringRedisTemplate.opsForValue().increment("chatRoomId:" + chatMessage.getChatRoomId() + ":seq");
        chatMessage.setSeq(seq);//todo : 이거 redis안타게 해보자
        chatMessageRepository.save(chatMessage);
        String topic = chatMessage.getChatRoomType() == ChatRoomType.ONE ? "memberChatRoom" : "chatRoom";

        redisTemplate.convertAndSend(topic, chatMessage);
    }
}
