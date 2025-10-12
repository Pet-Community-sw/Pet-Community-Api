package com.example.petapp.domain.sse;

import com.example.petapp.domain.member.model.entity.Member;
import com.example.petapp.domain.sse.model.dto.NotificationListDto;
import com.example.petapp.domain.query.QueryService;
import com.example.petapp.common.base.util.TimeAgoUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final RedisTemplate<String, Object> notificationRedisTemplate;
    private final ObjectMapper objectMapper;
    private final SseEmitterManager sseEmitterManager;
    private final QueryService queryService;


    @Transactional(readOnly = true)
    @Override
    public List<NotificationListDto> getNotifications(String email) {//몇분 전 추가해야할듯.
        Member member = queryService.findByMember(email);
        Set<String> keys = notificationRedisTemplate.keys("notifications:" + member.getId() + ":*");
        return keys.stream()
                .map(key -> notificationRedisTemplate.opsForValue().get(key))
                .map(message -> {
                    try {
                        NotificationListDto notificationListDto = objectMapper.readValue((String) message, NotificationListDto.class);
                        notificationListDto.setCreatedAt(TimeAgoUtil.getTimeAgo(notificationListDto.getNotificationTime()));
                        return notificationListDto;
                        //readValue는 String,class로 기대를함. 그래서 명시적(String)으로 형변환.
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

    }

    @Override
    public SseEmitter subscribe(String token) {
        return sseEmitterManager.subscribe(token);
    }
}
