package com.example.PetApp.config.redis.chathandler;

import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.memberchatRoom.model.entity.MemberChatRoom;
import com.example.PetApp.common.exception.NotFoundException;
import com.example.PetApp.domain.chatting.mapper.ChatMessageMapper;
import com.example.PetApp.domain.memberchatRoom.MemberChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OneToOneChatHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final StringRedisTemplate redisTemplate;
    private final MemberChatRoomRepository memberChatRoomRepository;

    public void handle(ChatMessage message) {
        MemberChatRoom chatRoom = memberChatRoomRepository.findById(message.getChatRoomId())
                .orElseThrow(() -> new NotFoundException("해당 채팅방이 없습니다."));

        messagingTemplate.convertAndSend("/sub/member/chat/" + chatRoom.getMemberChatRoomId(), message);

        saveLastMessageToRedis(message);

        Set<String> onlineMembers = redisTemplate.opsForSet()
                .members("memberChatRoomId:" + chatRoom.getMemberChatRoomId() + ":onlineMembers");

        Map<Long, Long> unReadMap = countUnread(chatRoom, message, onlineMembers);

        messagingTemplate.convertAndSend("/sub/member/chat/update",
                ChatMessageMapper.toUpdateChatRoomList(chatRoom.getMemberChatRoomId(), message, unReadMap));
    }

    private void saveLastMessageToRedis(ChatMessage message) {
        redisTemplate.opsForValue().set("memberChat:lastMessage" + message.getChatRoomId(), message.getMessage());
        redisTemplate.opsForValue().set("memberChat:lastMessageTime" + message.getChatRoomId(), String.valueOf(message.getMessageTime()));
    }

    private Map<Long, Long> countUnread(MemberChatRoom chatRoom, ChatMessage message, Set<String> onlineMembers) {
        Map<Long, Long> map = new HashMap<>();
        for (Member member : chatRoom.getMembers()) {
            if (!member.getMemberId().equals(message.getSenderId())) {
                boolean isOnline = onlineMembers != null && onlineMembers.contains(member.getMemberId().toString());
                if (!isOnline) {
                    String key = "unReadMemberChat:" + message.getChatRoomId() + ":" + member.getMemberId();
                    Long count = redisTemplate.opsForValue().increment(key);
                    map.put(member.getMemberId(), count);
                }
            }
        }
        return map;
    }
}
