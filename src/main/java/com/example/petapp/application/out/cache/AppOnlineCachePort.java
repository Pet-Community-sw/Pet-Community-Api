package com.example.petapp.application.out.cache;

public interface AppOnlineCachePort {

    Boolean exist(Long id);

    void create(Long id);

    void delete(Long id);
}
