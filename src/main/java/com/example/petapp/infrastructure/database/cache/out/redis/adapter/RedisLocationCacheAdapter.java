package com.example.petapp.infrastructure.database.cache.out.redis.adapter;

import com.example.petapp.application.out.cache.LocationCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RedisLocationCacheAdapter implements LocationCachePort {

    private final StringRedisTemplate template;

    @Override
    public void create(Long key, String value) {
        template.opsForList().rightPush(getKey(key), value);

    }

    @Override
    public String find(Long key) {
        return template.opsForList().index(getKey(key), -1);
        //마지막 요소 반환
    }

    @Override
    public List<String> findList(Long key) {
        return template.opsForList().range(getKey(key), 0, -1);
        //전체 데이터 반환
    }

    @Override
    public void delete(Long key) {
        template.delete(getKey(key));
    }

    private String getKey(Long id) {
        return "walk:path:" + id;
    }
}
