package com.example.petapp.common.base.util.notification;

import com.example.petapp.domain.member.model.entity.Member;
import com.example.petapp.domain.notification.NotificationService;
import com.example.petapp.domain.notification.model.dto.NotificationListDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendNotificationUtil {

    private final RedisTemplate<String, NotificationListDto> notificationRedisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final NotificationService notificationService;
//    private final FcmService fcmService;

    /*
     * foreground 유저는 sse, background 유저는 fcm
     * */
    public void sendNotification(Member member, String message) {
        String key = "notifications:" + member.getId() + ":";//알림 설정 최대 3일.
        NotificationListDto notificationListDto = new NotificationListDto(message, LocalDateTime.now());
        notificationRedisTemplate.opsForValue().set(key, notificationListDto, Duration.ofDays(3));
        if (Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember("foreGroundMembers", member.getId().toString()))) {
            notificationService.sendNotification(member.getId(), message);
        } else {
            log.info("backGroundMember");
//            fcmService.sendNotification(member.getFcmToken().getFcmToken(), "명냥로드", message);
        }
    }
}
