package com.example.petapp.application.out.cache;

public interface TokenCachePort {
    void blacklist(String accessToken, long duration);

    boolean isBlacklisted(String accessToken);
}
