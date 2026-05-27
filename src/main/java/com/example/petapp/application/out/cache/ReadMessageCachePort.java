package com.example.petapp.application.out.cache;

import com.example.petapp.domain.message.model.ChatMessage;

public interface ReadMessageCachePort {
    void create(ChatMessage chatMessage);

    Long find(Long chatRoomId, Long userId);

    void delete(Long chatRoomId, Long userId);

    void delete(Long chatRoomId);
}
