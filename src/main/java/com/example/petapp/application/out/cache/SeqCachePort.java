package com.example.petapp.application.out.cache;

public interface SeqCachePort {
    boolean exist(Long chatRoomId);

    Long increment(Long chatRoomId);

    void create(Long chatRoomId, Long seq);

    void delete(Long chatRoomId);
}
