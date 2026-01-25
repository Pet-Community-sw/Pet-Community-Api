package com.example.petapp.infrastructure.database.cache.out.redis.adapter;

import com.example.petapp.application.in.member.object.dto.response.MemberSearchResponseDto;
import com.example.petapp.application.out.cache.MemberSearchCachePort;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RedisMemberSearchCacheAdapter implements MemberSearchCachePort {

    private final RedisTemplate<String, List<MemberSearchResponseDto>> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void create(String keyword, int page, List<MemberSearchResponseDto> dtos) {
        redisTemplate.opsForValue().set(getKey(keyword, page), dtos, Duration.ofSeconds(15));
    }

    @Override
    public List<MemberSearchResponseDto> get(String keyword, int page) {
        return objectMapper.convertValue(redisTemplate.opsForValue().get(getKey(keyword, page)), new TypeReference<>() {
        });
    }

    private String getKey(String keyword, int page) {
        return "search:member:" + keyword + ":page:" + page;
    }
}
