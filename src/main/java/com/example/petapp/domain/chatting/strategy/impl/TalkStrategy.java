package com.example.petapp.domain.chatting.strategy.impl;

import com.example.petapp.common.base.util.notification.SendNotificationUtil;
import com.example.petapp.domain.chatting.ChatMessageRepository;
import com.example.petapp.domain.chatting.model.dto.MessageResponseDto;
import com.example.petapp.domain.chatting.model.dto.UpdateListDto;
import com.example.petapp.domain.chatting.model.entity.ChatMessage;
import com.example.petapp.domain.chatting.model.type.MessageType;
import com.example.petapp.domain.chatting.strategy.MessageTypeStrategy;
import com.example.petapp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.petapp.domain.profile.model.entity.Profile;
import com.example.petapp.domain.query.QueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class TalkStrategy implements MessageTypeStrategy {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final StringRedisTemplate redisTemplate;
    private final SendNotificationUtil sendNotificationUtil;
    private final QueryService queryService;

    AtomicInteger seq = new AtomicInteger(0);

    @Override
    public void handle(ChatMessage chatMessage) {
        chatMessage.updateSeq(seq.incrementAndGet());
        chatMessageRepository.save(chatMessage);

        //메시지를 전송
        simpMessagingTemplate.convertAndSend("/sub/chat/" + chatMessage.getChatRoomId(),
                MessageResponseDto.builder().messageType(MessageType.TALK).body(chatMessage).build());

        sendChatNotificationAndUpdateList(chatMessage);
        updateLastMessage(chatMessage);
    }

    private void updateLastMessage(ChatMessage chatMessage) {
        Map<String, String> lastMessageInfo = new HashMap<>();
        lastMessageInfo.put("seq", String.valueOf(chatMessage.getSeq()));
        lastMessageInfo.put("lastMessage", chatMessage.getMessage());
        lastMessageInfo.put("lastMessageTime", String.valueOf(chatMessage.getMessageTime()));

        redisTemplate.opsForHash().putAll("chat:lastMessageInfo:" + chatMessage.getChatRoomId(), lastMessageInfo);
    }

    //todo : 업데이트 로직 정리해야됨.
    private void sendChatNotificationAndUpdateList(ChatMessage chatMessage) {
        Long chatRoomId = chatMessage.getChatRoomId();
        Long senderId = chatMessage.getSenderId();
        String message = chatMessage.getSenderName() + "님이 메시지를 보냈습니다.";

        ChatRoom chatRoom = queryService.findByChatRoom(chatRoomId);
        Set<Long> users = chatRoom.getUsers();
        Set<String> onlineUsers = redisTemplate.opsForSet()
                .members("chatRoomId:" + chatRoomId + ":onlineUsers");

        if (onlineUsers == null) {
            return;
        }

        users.stream().filter(userId -> !userId.equals(senderId))
                .filter(userId -> !onlineUsers.contains(userId.toString()))
                .forEach(userId -> {
                    Profile profile = queryService.findByProfile(userId);
                    sendNotificationUtil.sendNotification(profile.getMember(), message);
                    Object seqByProfile = redisTemplate.opsForHash().get("chatRoomId:" + chatRoomId + ":read", profile);
                    int profileSeq = seqByProfile == null ? 0 : (Integer) seqByProfile;
                    simpMessagingTemplate.convertAndSend("sub/list/" + profile.getId(),
                            MessageResponseDto.builder().messageType(MessageType.LIST_UPDATE).body(new UpdateListDto(chatRoomId, (chatMessage.getSeq() - profileSeq), chatMessage.getMessage(), chatMessage.getMessageTime())));
                });


    }
}
