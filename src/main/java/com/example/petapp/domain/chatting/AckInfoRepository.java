package com.example.petapp.domain.chatting;

import java.util.Set;

public interface AckInfoRepository {

    void save(String messageId, Set<Long> users);

    void deleteUser(String messageId, Long userId);

    Set<Long> find(String messageId);

    void clear(String messageId);
}
