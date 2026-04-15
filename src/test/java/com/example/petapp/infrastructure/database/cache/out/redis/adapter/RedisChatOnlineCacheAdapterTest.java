package com.example.petapp.infrastructure.database.cache.out.redis.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisChatOnlineCacheAdapterTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private SetOperations<String, String> setOperations;

    @InjectMocks
    private RedisChatOnlineCacheAdapter adapter;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
    }

    @Test
    void create는_온라인유저_셋에_추가한다() {
        adapter.create("1", "2");
        verify(setOperations).add("chatRoomId:1:onlineUsers", "2");
    }

    @Test
    void delete는_온라인유저_셋에서_제거한다() {
        adapter.delete("1", "2");
        verify(setOperations).remove("chatRoomId:1:onlineUsers", "2");
    }

    @Test
    void find는_온라인유저_목록을_반환한다() {
        when(setOperations.members("chatRoomId:1:onlineUsers")).thenReturn(Set.of("2", "3"));

        Set<String> result = adapter.find(1L);

        assertThat(result).containsExactlyInAnyOrder("2", "3");
    }
}
