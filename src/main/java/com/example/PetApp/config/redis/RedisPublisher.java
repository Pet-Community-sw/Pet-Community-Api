package com.example.PetApp.config.redis;

import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import com.example.PetApp.domain.chatting.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    private final ChatMessageRepository chatMessageRepository;

    public void publish(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
        String topic = chatMessage.getChatRoomType() == ChatMessage.ChatRoomType.ONE ? "memberChatRoom" : "chatRoom";

        redisTemplate.convertAndSend(topic, chatMessage);
    }
}
