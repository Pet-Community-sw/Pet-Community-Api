package com.example.petapp.application.out.cache;

import java.util.List;

public interface TypingCachePort {
    void create(Long roomId, Long userId, long duration);

    void delete(Long roomId, Long userId);

    List<Long> getList(Long roomId);
}
