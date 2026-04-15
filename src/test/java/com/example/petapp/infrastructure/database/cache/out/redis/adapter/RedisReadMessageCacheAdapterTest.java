package com.example.petapp.infrastructure.database.cache.out.redis.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisReadMessageCacheAdapterTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @InjectMocks
    private RedisReadMessageCacheAdapter adapter;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    @Test
    void 읽은메세지_seq가_문자열이면_Long으로_변환한다() {
        when(hashOperations.get(RedisReadMessageCacheAdapter.getKey(1L), "2")).thenReturn("15");

        Long result = adapter.find(1L, 2L);

        assertThat(result).isEqualTo(15L);
    }

    @Test
    void 읽은메세지_seq가_숫자가아니면_0을_반환한다() {
        when(hashOperations.get(RedisReadMessageCacheAdapter.getKey(1L), "2")).thenReturn("not-number");

        Long result = adapter.find(1L, 2L);

        assertThat(result).isEqualTo(0L);
    }
}
