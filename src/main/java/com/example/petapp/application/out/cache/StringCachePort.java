package com.example.petapp.application.out.cache;


import java.util.Set;

public interface StringCachePort {

    Set<String> find(String userId);

    void create(String key, String value);
}
