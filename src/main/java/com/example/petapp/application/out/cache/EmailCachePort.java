package com.example.petapp.application.out.cache;

public interface EmailCachePort {
    void createAuthCode(String email, String code, long ttlSeconds);

    boolean exists(String email);

    void deleteAuthCode(String email);

    String findAuthCode(String email);
}
