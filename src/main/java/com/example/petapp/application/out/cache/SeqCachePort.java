package com.example.petapp.application.out.cache;

public interface SeqCachePort {
    boolean exists(Long chatRoomId);

    Long incrementAndGet(Long chatRoomId);

    void initializeIfAbsent(Long chatRoomId, Long seq);

    void deleteSeq(Long chatRoomId);
}
