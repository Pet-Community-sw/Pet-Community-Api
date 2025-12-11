package com.example.petapp.application.out.cache;

import java.util.Set;

public interface LikeCachePort {
    Set<Long> getList(Long id);

    void create(Long id, Long value);

    void delete(Long id, Long value);
}
