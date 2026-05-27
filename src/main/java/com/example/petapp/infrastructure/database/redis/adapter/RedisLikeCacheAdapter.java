package com.example.petapp.infrastructure.database.redis.adapter;

import com.example.petapp.application.out.cache.LikeCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisLikeCacheAdapter implements LikeCachePort {

    private final static String KEY_PREFIX = "post:likes:";

    private final RedisTemplate<String, Long> likeRedisTemplate;

    public static String getKey(Long id) {
        return KEY_PREFIX + id;
    }

    @Override
    public Set<Long> findLikedMemberIds(Long postId) {
        return likeRedisTemplate.opsForSet().members(getKey(postId));
    }

    @Override
    public void createLike(Long postId, Long memberId) {
        likeRedisTemplate.opsForSet().add(getKey(postId), memberId);
    }

    @Override
    public void deleteLike(Long postId, Long memberId) {
        likeRedisTemplate.opsForSet().remove(getKey(postId), memberId);
    }
}
