package com.example.petapp.infrastructure.database.redis.adapter;

import com.example.petapp.application.out.cache.MemberSearchCachePort;
import com.example.petapp.application.usecase.member.object.dto.response.MemberSearchResponseDto;
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

    private final static String KEY_PREFIX = "search:member:";
    private final static String KEY_SUGGESTIONS = "suggestions:";
    private final static String KEY_RESULT = "result:";
    private final static String KEY_PAGE = ":page:";
    private static final Duration SUGGESTIONS_TTL = Duration.ofMinutes(1);
    private static final Duration SEARCH_RESULT_TTL = Duration.ofSeconds(15);

    private final RedisTemplate<String, List<MemberSearchResponseDto>> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public List<MemberSearchResponseDto> findSuggestions(String keyword) {
        return objectMapper.convertValue(redisTemplate.opsForValue().get(suggestionsKey(keyword)), new TypeReference<>() {
        });
    }

    @Override
    public void createSuggestions(String keyword, List<MemberSearchResponseDto> dtos) {
        redisTemplate.opsForValue().set(suggestionsKey(keyword), dtos, SUGGESTIONS_TTL);
    }

    @Override
    public void createSearchResult(String keyword, int page, List<MemberSearchResponseDto> dtos) {
        redisTemplate.opsForValue().set(searchResultKey(keyword, page), dtos, SEARCH_RESULT_TTL);
    }

    @Override
    public List<MemberSearchResponseDto> findSearchResult(String keyword, int page) {
        return objectMapper.convertValue(redisTemplate.opsForValue().get(searchResultKey(keyword, page)), new TypeReference<>() {
        });
    }

    private String suggestionsKey(String keyword) {
        return KEY_PREFIX + KEY_SUGGESTIONS + keyword;
    }

    private String searchResultKey(String keyword, int page) {
        return KEY_PREFIX + KEY_RESULT + keyword + KEY_PAGE + page;
    }
}
