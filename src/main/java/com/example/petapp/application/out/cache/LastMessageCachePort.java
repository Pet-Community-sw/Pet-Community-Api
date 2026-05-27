package com.example.petapp.application.out.cache;

import com.example.petapp.application.usecase.chatmessage.model.dto.LastMessageInfoDto;
import com.example.petapp.domain.chatmessage.model.ChatMessage;

public interface LastMessageCachePort {
    void create(ChatMessage chatMessage);

    LastMessageInfoDto find(Long id);

    void delete(Long chatRoomId);
}
