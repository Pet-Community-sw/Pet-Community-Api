package com.example.petapp.infrastructure.database.cache.out.redis.adapter;

import com.example.petapp.application.out.cache.TypingCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisTypingCacheAdapter implements TypingCachePort {

    private final StringRedisTemplate template;

    /**
     * 해당 roomId에 각각 userId에 대한 타이핑 상태를 duration동안 저장하기 위해 ZSet을 사용
     */
    @Override
    public void create(Long roomId, Long userId, long duration) {
        long expireTime = System.currentTimeMillis() + duration;
        // 현재 시간에 duration을 더해 만료 시간 계산(durtion바로 넣으면 사용 불가)
        // expireTime이 지나도 자동으로 삭제되지 않음
        template.opsForZSet().add(getKey(roomId), String.valueOf(userId), expireTime);
    }

    @Override
    public void delete(Long roomId, Long userId) {
        template.opsForZSet().remove(getKey(roomId), String.valueOf(userId));
    }

    @Override
    public List<Long> getList(Long roomId) {
        String key = getKey(roomId);
        template.opsForZSet().removeRangeByScore(key, 0, System.currentTimeMillis());
        // 현재 시간 이전의 값들은 만료된 값이므로 삭제
        Set<String> users = template.opsForZSet().range(key, 0, -1);
        //없으면 빈 set 반환

        return users.stream().map(Long::valueOf).toList();
    }

    private String getKey(Long roomId) {
        return "typing:roomId:" + roomId;
    }
}
