package com.example.petapp.application.out.cache;

public interface TokenCachePort {
    void create(String key, String value, long duration);

    boolean exist(String key);
}
