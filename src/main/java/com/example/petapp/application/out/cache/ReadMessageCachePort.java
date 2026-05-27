package com.example.petapp.application.out.cache;

import com.example.petapp.domain.chatmessage.model.ChatMessage;

public interface ReadMessageCachePort {
    void markAsRead(ChatMessage chatMessage);

    Long findLastReadSeq(Long chatRoomId, Long memberId);

    void deleteReadSeq(Long chatRoomId, Long memberId);

    void deleteRoomReadState(Long chatRoomId);
}
