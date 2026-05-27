package com.example.petapp.application.out.cache;

import com.example.petapp.application.usecase.chatting.model.dto.LastMessageInfoDto;
import com.example.petapp.domain.message.model.ChatMessage;

public interface LastMessageCachePort {
    void create(ChatMessage chatMessage);

    LastMessageInfoDto find(Long id);

    void delete(Long chatRoomId);
}
