package com.example.petapp.infrastructure.database.cache.out.redis.adapter;

import com.example.petapp.application.in.chatting.model.dto.LastMessageInfoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisLastMessageCacheAdapterTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @InjectMocks
    private RedisLastMessageCacheAdapter adapter;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    @Test
    void 마지막메세지_seq가_문자열이면_Long으로_변환한다() {
        Map<Object, Object> values = new HashMap<>();
        values.put("seq", "7");
        values.put("lastMessage", "hello");
        values.put("lastMessageTime", "2026-01-01T00:00");
        when(hashOperations.entries(RedisLastMessageCacheAdapter.key(1L))).thenReturn(values);

        LastMessageInfoDto result = adapter.find(1L);

        assertThat(result.getLastSeq()).isEqualTo(7L);
        assertThat(result.getLastMessage()).isEqualTo("hello");
        assertThat(result.getLastMessageTime()).isEqualTo("2026-01-01T00:00");
    }

    @Test
    void 마지막메세지_seq가_숫자가아니면_0을_반환한다() {
        Map<Object, Object> values = new HashMap<>();
        values.put("seq", "not-number");
        when(hashOperations.entries(RedisLastMessageCacheAdapter.key(1L))).thenReturn(values);

        LastMessageInfoDto result = adapter.find(1L);

        assertThat(result.getLastSeq()).isEqualTo(0L);
    }
}
