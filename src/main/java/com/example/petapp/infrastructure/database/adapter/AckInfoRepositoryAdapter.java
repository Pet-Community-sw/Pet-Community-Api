package com.example.petapp.infrastructure.database.adapter;

import com.example.petapp.domain.chatting.AckInfoRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class AckInfoRepositoryAdapter implements AckInfoRepository {

    private final ConcurrentMap<String, Set<Long>> sendUsers = new ConcurrentHashMap<>();

    @Override
    public void save(String messageId, Set<Long> users) {
        sendUsers.computeIfAbsent(messageId, id -> ConcurrentHashMap.newKeySet()).addAll(users);
    }

    @Override
    public void deleteUser(String messageId, Long userId) {
        Set<Long> set = sendUsers.get(messageId);
        if (set == null) {
            return;
        }

        set.remove(userId);

        if (set.isEmpty()) {
            clear(messageId);
        }
    }

    @Override
    public Set<Long> find(String messageId) {
        Set<Long> resendUsers = sendUsers.get(messageId);
        if (resendUsers == null) {
            return Set.of(); // 빈 불변 Set
        }
        return resendUsers;
    }

    @Override
    public void clear(String messageId) {
        sendUsers.remove(messageId);
    }
}
