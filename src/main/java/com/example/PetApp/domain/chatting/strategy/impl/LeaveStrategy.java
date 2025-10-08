package com.example.PetApp.domain.chatting.strategy.impl;

import com.example.PetApp.config.redis.RedisPublisher;
import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import com.example.PetApp.domain.chatting.strategy.MessageTypeStrategy;
import com.example.PetApp.domain.groupchatroom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LeaveStrategy implements MessageTypeStrategy {

    private final RedisPublisher redisPublisher;
    private final ChatRoomService chatRoomService;

    @Override
    public void handle(ChatMessage chatMessage) {
        chatMessage.setMessage(chatMessage.getSenderName() + "님이 나가셨습니다.");
        redisPublisher.publish(chatMessage);
        chatRoomService.deleteChatRoom(chatMessage.getChatRoomId(), chatMessage.getSenderId());
    }
}
