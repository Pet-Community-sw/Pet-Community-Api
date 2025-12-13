package com.example.petapp.application.out.cache;

public interface TokenCachePort {
    void createWithDuration(String key, String value, long duration);
}
