package com.example.petapp.domain.chatting.offline;

import com.example.petapp.domain.chatting.model.ChatMessage;
import com.example.petapp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.petapp.port.InMemoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfflineUserServiceImpl implements OfflineUserService {

    private final InMemoryService inMemoryService;

    @Override
    public void setOfflineUsersAndUnreadCount(ChatMessage chatMessage, ChatRoom chatRoom) {
        Set<Long> users = chatRoom.getUsers();
        Set<String> onlineUsers = inMemoryService.getOnlineDatas(chatRoom.getId());

        Set<Long> offlineProfiles = users.stream()
                .filter(userId -> !onlineUsers.contains(userId.toString()))
                .collect(Collectors.toSet());

        chatMessage.setUsers(offlineProfiles);
        chatMessage.setUnReadCount(offlineProfiles.size());

        log.info("메시지에 대한 안 읽은 유저 추적 : {}", offlineProfiles);
    }
}
