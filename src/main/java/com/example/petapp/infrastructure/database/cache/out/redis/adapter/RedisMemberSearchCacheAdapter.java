package com.example.petapp.infrastructure.database.cache.out.redis.adapter;

import com.example.petapp.application.in.member.object.dto.response.MemberSearchResponseDto;
import com.example.petapp.application.out.cache.MemberSearchCachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisMemberSearchCacheAdapter implements MemberSearchCachePort {

    private final RedisTemplate<String, List<MemberSearchResponseDto>> redisTemplate;

    @Override
    public List<MemberSearchResponseDto> get(String keyword) {
        return redisTemplate.opsForValue().get(getKey(keyword));
    }

    @Override
    public void create(String keyword, List<MemberSearchResponseDto> memberSearchResponseDtos, Duration duration) {
        redisTemplate.opsForValue().set(getKey(keyword), memberSearchResponseDtos, duration);
    }

    private String getKey(String keyword) {
        return "ac:member:" + keyword;
    }

}
