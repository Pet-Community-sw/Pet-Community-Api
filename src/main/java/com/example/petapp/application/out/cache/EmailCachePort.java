package com.example.petapp.application.out.cache;

public interface EmailCachePort {
    void createWithDuration(String key, String value, long duration);

    boolean exist(String key);

    void delete(String key);

    String get(String key);
}
