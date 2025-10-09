package com.example.PetApp.domain.chatting.offline;

import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import com.example.PetApp.domain.groupchatroom.model.entity.ChatRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfflineUserServiceImpl implements OfflineUserService {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void setOfflineUsersAndUnreadCount(ChatMessage chatMessage, ChatRoom chatRoom) {
        Set<Long> users = chatRoom.getUsers();
        Set<String> onlineUsers = Optional.ofNullable(stringRedisTemplate.opsForSet()
                .members("chatRoomId:" + chatRoom.getId() + ":onlineUsers")).orElse(Collections.emptySet());

        Set<Long> offlineProfiles = users.stream()
                .filter(userId -> !onlineUsers.contains(userId.toString()))
                .collect(Collectors.toSet());

        chatMessage.setUsers(offlineProfiles);
        chatMessage.setChatUnReadCount(offlineProfiles.size());

        log.info("메시지에 대한 안 읽은 유저 추적 : {}", offlineProfiles);
    }
}
