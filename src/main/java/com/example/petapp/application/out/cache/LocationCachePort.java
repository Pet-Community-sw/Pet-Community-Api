package com.example.petapp.application.out.cache;

import java.util.List;

public interface LocationCachePort {
    void create(Long key, String value);

    String get(Long key);

    List<String> getList(Long key);

    void delete(Long key);
}
