package com.example.petapp.infrastructure.event.notification;

import com.example.petapp.application.in.chatting.model.dto.NotificationDto;
import com.example.petapp.application.in.chatting.model.dto.SendResponseDto;
import com.example.petapp.application.in.chatting.model.type.CommandType;
import com.example.petapp.application.in.notification.dto.NotificationEvent;
import com.example.petapp.application.in.notification.dto.NotificationListDto;
import com.example.petapp.application.out.SendPort;
import com.example.petapp.application.out.cache.AppOnlineCachePort;
import com.example.petapp.application.out.cache.NotificationsCachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {
    private final NotificationsCachePort notificationsCachePort;
    private final AppOnlineCachePort appOnlineCachePort;
    private final SendPort sendPort;
    //    private final FcmService fcmService;

    /*
     * foreground 유저는 sse, background 유저는 fcm --x
     * foreground 유저는 stomp, background 유저는 fcm
     * */
    @Async("notificationExecutor")
    @EventListener
    @Retryable(
            //네트워크나 원격 호출 에러, 런타임 에러 시 재시도
            value = {RemoteAccessException.class, RuntimeException.class},
            maxAttempts = 4,// 최대 재시도 횟수(기본값 3)
            backoff = @Backoff(delay = 2000, multiplier = 2.0, random = true)// 재시도 간격
            // 지수 백오프 및 jitter 적용(재시도 간격에 무작위성을 더하는 것)->서버 부담 완화
    )
    public void handle(NotificationEvent event) {
        if (appOnlineCachePort.exist(event.id())) {
            sendPort.send("/sub/notification/" + event.id(),
                    SendResponseDto.builder().commandType(CommandType.NOTIFICATION).body(new NotificationDto(event.id(), event.message())).build());
        } else {
            log.info("backGroundMember");
//            fcmService.sendNotification(member.getFcmToken().getFcmToken(), "명냥로드", message);
        }
        notificationsCachePort.create(event.id(), new NotificationListDto(event.message(), LocalDateTime.now()), 3);
    }

    /**
     * ]
     * 재시도 실패 시 호출
     * 이 후에 로그 db저장 예정
     * 파라미터 첫 번째 예외 객체를 둬야하는 이유?
     *
     * @Retryalbe 파라미터와 일치해야힘.
     */
    @Recover
    public void recover(Exception e, NotificationEvent event) {
        log.error("Notification 전송 실패. memberId: {}, message: {}, error: {}", event.id(), event.message(), e.getMessage());
    }
}
