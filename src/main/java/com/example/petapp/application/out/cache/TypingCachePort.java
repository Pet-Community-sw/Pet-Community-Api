package com.example.petapp.application.out.cache;

import java.util.List;

public interface TypingCachePort {
    void markTyping(Long chatRoomId, Long memberId, long durationMillis);

    void clearTyping(Long chatRoomId, Long memberId);

    List<Long> findTypingMemberIds(Long chatRoomId);
}
