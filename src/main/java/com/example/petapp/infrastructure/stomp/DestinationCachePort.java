package com.example.petapp.infrastructure.stomp;

import java.util.Set;

public interface DestinationCachePort {

    Set<String> getSet(String key);

    void delete(String key);

    void create(String key, String value);
}
