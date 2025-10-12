package com.example.petapp.common.base.util.notification;

import com.example.petapp.domain.member.model.entity.Member;
import com.example.petapp.domain.notification.NotificationService;
import com.example.petapp.domain.notification.model.dto.NotificationListDto;
import com.example.petapp.port.InMemoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendNotificationUtil {

    private final NotificationService notificationService;
    private final InMemoryService inMemoryService;
//    private final FcmService fcmService;

    /*
     * foreground 유저는 sse, background 유저는 fcm
     * */
    public void sendNotification(Member member, String message) {
        inMemoryService.createNotificationData(member.getId(), new NotificationListDto(message, LocalDateTime.now()), 3);
        if (inMemoryService.existForeGroundData(member.getId())) {
            notificationService.sendNotification(member.getId(), message);
        } else {
            log.info("backGroundMember");
//            fcmService.sendNotification(member.getFcmToken().getFcmToken(), "명냥로드", message);
        }
    }
}
