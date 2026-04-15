package com.example.petapp.infrastructure.database.cache.out.redis.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisDestinationCachePortTest {

    @Mock
    private StringRedisTemplate template;
    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisDestinationCachePort port;

    @Test
    void create는_subscription키로_저장한다() {
        when(template.opsForValue()).thenReturn(valueOperations);

        port.create("sub-1", "100");
        verify(valueOperations).set("stomp:subscriptionId:sub-1", "100");
    }

    @Test
    void get은_subscription키로_조회한다() {
        when(template.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("stomp:subscriptionId:sub-1")).thenReturn("100");

        String result = port.get("sub-1");

        assertThat(result).isEqualTo("100");
    }

    @Test
    void delete는_subscription키를_삭제한다() {
        port.delete("sub-1");
        verify(template).delete("stomp:subscriptionId:sub-1");
    }
}
