package com.example.petapp.infrastructure.database.redis.adapter;

import com.example.petapp.application.out.cache.LocationCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RedisLocationCacheAdapter implements LocationCachePort {

    private final static String KEY_PREFIX = "walk:path:";

    private final StringRedisTemplate template;

    @Override
    public void createLocation(Long walkRecordId, String location) {
        template.opsForList().rightPush(getKey(walkRecordId), location);

    }

    @Override
    public String findLatestLocation(Long walkRecordId) {
        return template.opsForList().index(getKey(walkRecordId), -1);
        //마지막 요소 반환
    }

    @Override
    public List<String> findPath(Long walkRecordId) {
        return template.opsForList().range(getKey(walkRecordId), 0, -1);
        //전체 데이터 반환
    }

    @Override
    public void deletePath(Long walkRecordId) {
        template.delete(getKey(walkRecordId));
    }

    private String getKey(Long id) {
        return KEY_PREFIX + id;
    }
}
