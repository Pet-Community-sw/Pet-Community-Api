package com.example.petapp.infrastructure.database.cache.out.redis.adapter;

import com.example.petapp.application.out.cache.MemberRecentViewCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class RedisMemberRecentViewCacheAdapter implements MemberRecentViewCachePort {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void create(Long memberId, Long targetId) {
        String key = getKey(memberId);
        redisTemplate.opsForZSet().add(key, String.valueOf(targetId), System.currentTimeMillis());

        redisTemplate.expire(key, Duration.ofDays(7));

        Long size = redisTemplate.opsForZSet().zCard(key);
        if (size != null && size > 50) {
            long removeCount = size - 50;
            redisTemplate.opsForZSet().removeRange(key, 0, removeCount - 1);
        }
    }

    @Override
    public List<Long> findList(Long memberId) {
        Set<String> ids = redisTemplate.opsForZSet().reverseRange(getKey(memberId), 0, -1);

        if (ids == null) return List.of();

        return ids.stream()
                .map(Long::parseLong)
                .toList();

    }

    private String getKey(Long memberId) {
        return "member:recentView:" + memberId;
    }
}
