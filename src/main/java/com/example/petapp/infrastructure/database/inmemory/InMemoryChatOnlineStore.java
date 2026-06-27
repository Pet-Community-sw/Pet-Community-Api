package com.example.petapp.infrastructure.database.inmemory;

import com.example.petapp.infrastructure.stomp.store.ChatOnlineStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class InMemoryChatOnlineStore implements ChatOnlineStore {

    private final ConcurrentHashMap<Long, Set<Long>> onlineMap = new ConcurrentHashMap<>();

    @Override
    public void addOnlineUser(Long chatRoomId, Long memberId) {
        onlineMap.compute(chatRoomId, (k, onLineUsers) -> {
            if (onLineUsers == null) onLineUsers = ConcurrentHashMap.newKeySet();
            onLineUsers.add(memberId);
            return onLineUsers;
        });
    }

    @Override
    public void removeOnlineUser(Long chatRoomId, Long memberId) {
        onlineMap.computeIfPresent(chatRoomId, (k, onLineUsers) -> {
            onLineUsers.remove(memberId);
            return onLineUsers.isEmpty() ? null : onLineUsers;
        });
    }

    @Override
    public Set<Long> getOnlineUserIds(Long chatRoomId) {
        return onlineMap.getOrDefault(chatRoomId, Set.of());
    }
}
