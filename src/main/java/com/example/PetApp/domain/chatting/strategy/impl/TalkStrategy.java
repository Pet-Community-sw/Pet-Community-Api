package com.example.PetApp.domain.chatting.strategy.impl;

import com.example.PetApp.common.base.util.notification.SendNotificationUtil;
import com.example.PetApp.config.redis.RedisPublisher;
import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import com.example.PetApp.domain.chatting.strategy.MessageTypeStrategy;
import com.example.PetApp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.domain.query.QueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class TalkStrategy implements MessageTypeStrategy {

    private final StringRedisTemplate redisTemplate;
    private final RedisPublisher redisPublisher;
    private final SendNotificationUtil sendNotificationUtil;
    private final QueryService queryService;

    @Override
    public void handle(ChatMessage chatMessage) {
        redisPublisher.publish(chatMessage);
        sendChatNotification(chatMessage);
    }

    private void sendChatNotification(ChatMessage chatMessage) {
        Long chatRoomId = chatMessage.getChatRoomId();
        Long senderId = chatMessage.getSenderId();
        String message = chatMessage.getSenderName() + "님이 메시지를 보냈습니다.";

        ChatRoom chatRoom = queryService.findByChatRoom(chatRoomId);
        Set<Long> users = chatRoom.getUsers();
        Set<String> onlineProfiles = redisTemplate.opsForSet()
                .members("chatRoomId:" + chatRoomId + ":onlineProfiles");

        if (onlineProfiles == null) {
            return;
        }

        users.stream().filter(userId -> !userId.equals(senderId))
                .filter(userId -> !onlineProfiles.contains(userId.toString()))
                .forEach(userId -> {
                    Profile profile = queryService.findByProfile(userId);
                    sendNotificationUtil.sendNotification(profile.getMember(), message);
                });

    }
}
