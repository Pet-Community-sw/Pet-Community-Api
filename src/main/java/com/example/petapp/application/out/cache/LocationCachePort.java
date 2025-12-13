package com.example.petapp.application.out.cache;

import java.util.List;

public interface LocationCachePort {
    void create(Long key, String value);

    String find(Long key);

    List<String> findList(Long key);

    void delete(Long key);
}
