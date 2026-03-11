package com.example.petapp.infrastructure.stomp;

public interface DestinationCachePort {

    void delete(String key);

    void create(String key, String value);

    String get(String key);


}
