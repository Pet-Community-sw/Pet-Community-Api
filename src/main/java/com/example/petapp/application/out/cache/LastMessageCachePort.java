package com.example.petapp.application.out.cache;

import com.example.petapp.application.usecase.chatmessage.model.dto.LastMessageInfoDto;
import com.example.petapp.domain.chatmessage.model.ChatMessage;

public interface LastMessageCachePort {
    void saveLastMessage(ChatMessage chatMessage);

    LastMessageInfoDto findLastMessageInfo(Long chatRoomId);

    void deleteLastMessageInfo(Long chatRoomId);
}
