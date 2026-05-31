package com.example.petapp.application.out.cache;

public interface EmailCachePort {
    boolean exists(String email);

    void deleteAuthCode(String email);

    String findAuthCode(String email);
}
