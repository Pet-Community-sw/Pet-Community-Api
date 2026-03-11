package com.example.petapp.application.out.cache;

import java.util.Set;

public interface ChatOnlineCachePort {

    void create(String chatroomId, String profileId);

    void delete(String chatRoomId, String profileId);

    Set<String> find(Long id);
}
