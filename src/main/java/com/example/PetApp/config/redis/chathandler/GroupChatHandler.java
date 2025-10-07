package com.example.PetApp.config.redis.chathandler;

import com.example.PetApp.common.exception.NotFoundException;
import com.example.PetApp.domain.chatting.mapper.ChatMessageMapper;
import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import com.example.PetApp.domain.groupchatroom.ChatRoomRepository;
import com.example.PetApp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.PetApp.domain.profile.model.entity.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GroupChatHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final StringRedisTemplate redisTemplate;
    private final ChatRoomRepository chatRoomRepository;

    public void handle(ChatMessage message) {
        ChatRoom chatRoom = chatRoomRepository.findById(message.getChatRoomId())
                .orElseThrow(() -> new NotFoundException("해당 채팅방이 없습니다."));

        messagingTemplate.convertAndSend("/sub/chat/" + chatRoom.getId(), message);

        saveLastMessageToRedis(message);

        Set<String> onlineProfiles = redisTemplate.opsForSet()
                .members("chatRoomId:" + chatRoom.getId() + ":onlineProfiles");

        Map<Long, Long> unReadMap = countUnread(chatRoom, message, onlineProfiles);

        messagingTemplate.convertAndSend("/sub/chat/update",
                ChatMessageMapper.toUpdateChatRoomList(chatRoom.getId(), message, unReadMap));
    }

    private void saveLastMessageToRedis(ChatMessage chatMessage) {
        Map<String, String> lastMessageInfo = new HashMap<>();
        lastMessageInfo.put("seq", String.valueOf(chatMessage.getSeq()));
        lastMessageInfo.put("lastMessage", chatMessage.getMessage());
        lastMessageInfo.put("lastMessageTime", String.valueOf(chatMessage.getMessageTime()));

        redisTemplate.opsForHash().putAll("chat:lastMessageInfo:" + chatMessage.getChatRoomId(), lastMessageInfo);

//        redisTemplate.opsForValue().set("chat:lastMessage" + chatMessage.getChatRoomId(), chatMessage.getMessage());
//        redisTemplate.opsForValue().set("chat:lastMessageTime" + chatMessage.getChatRoomId(), String.valueOf(chatMessage.getMessageTime()));
    }

    private Map<Long, Long> countUnread(ChatRoom chatRoom, ChatMessage message, Set<String> onlineProfiles) {
        Map<Long, Long> map = new HashMap<>();
        for (Profile profile : chatRoom.getProfiles()) {
            if (!profile.getId().equals(message.getSenderId())) {
                boolean isOnline = onlineProfiles != null && onlineProfiles.contains(profile.getId().toString());
                if (!isOnline) {
                    String key = "unReadChatCount:" + message.getChatRoomId() + ":" + profile.getId();
                    Long count = redisTemplate.opsForValue().increment(key);
                    map.put(profile.getMember().getId(), count);
                }
            }
        }
        return map;
    }
}

