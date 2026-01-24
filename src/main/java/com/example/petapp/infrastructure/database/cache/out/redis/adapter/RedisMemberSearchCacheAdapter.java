package com.example.petapp.infrastructure.database.cache.out.redis.adapter;

import com.example.petapp.application.in.member.object.dto.response.MemberSearchResponseDto;
import com.example.petapp.application.out.cache.MemberSearchCachePort;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    @Override
    public List<MemberSearchResponseDto> get(String keyword) {
        //매퍼 필요 객체-> 객체 convertValue
        return objectMapper.convertValue(redisTemplate.opsForValue().get(getKey(keyword)), new TypeReference<>() {
        });
    }

    @Override
    public void create(String keyword, List<MemberSearchResponseDto> memberSearchResponseDtos) {
        redisTemplate.opsForValue().set(getKey(keyword), memberSearchResponseDtos, Duration.ofSeconds(15));
    }

    private String getKey(String keyword) {
        return "ac:member:" + keyword;
    }

}
