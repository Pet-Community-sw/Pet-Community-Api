package com.example.petapp.application.out.cache;

import java.util.Set;

public interface ChatOnlineCachePort {

    void create(Long chatroomId, Long profileId);

    void delete(Long chatRoomId, Long profileId);

    Set<String> find(Long id);
}
