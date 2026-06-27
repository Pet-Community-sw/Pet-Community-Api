package com.example.petapp.infrastructure.database.inmemory;

import com.example.petapp.infrastructure.stomp.store.ChatOnlineStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class InMemoryChatOnlineStore implements ChatOnlineStore {

    private final ConcurrentHashMap<Long, Set<Long>> onlineMap = new ConcurrentHashMap<>();

    @Override
    public void addOnlineUser(Long chatRoomId, Long memberId) {
        onlineMap.computeIfAbsent(chatRoomId, k -> ConcurrentHashMap.newKeySet()).add(memberId);
    }

    @Override
    public void removeOnlineUser(Long chatRoomId, Long memberId) {
        Set<Long> onlineUsers = onlineMap.get(chatRoomId);
        if (onlineUsers == null) return;
        onlineUsers.remove(memberId);
        if (onlineUsers.isEmpty()) onlineMap.remove(chatRoomId);
    }

    @Override
    public Set<Long> getOnlineUserIds(Long chatRoomId) {
        return onlineMap.getOrDefault(chatRoomId, new HashSet<>());
    }
}
