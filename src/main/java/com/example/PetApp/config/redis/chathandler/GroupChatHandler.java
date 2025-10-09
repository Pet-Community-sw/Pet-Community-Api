package com.example.PetApp.config.redis.chathandler;

import com.example.PetApp.common.exception.NotFoundException;
import com.example.PetApp.domain.chatting.mapper.ChatMessageMapper;
import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import com.example.PetApp.domain.groupchatroom.ChatRoomRepository;
import com.example.PetApp.domain.groupchatroom.model.entity.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

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

        Set<String> onlineUsers = redisTemplate.opsForSet()
                .members("chatRoomId:" + chatRoom.getId() + ":onlineUsers");

        Map<Long, Long> unReadMap = countUnread(chatRoom, message, onlineUsers);

        messagingTemplate.convertAndSend("/sub/chat/update",
                ChatMessageMapper.toUpdateChatRoomList(chatRoom.getId(), message, unReadMap));
    }

}

