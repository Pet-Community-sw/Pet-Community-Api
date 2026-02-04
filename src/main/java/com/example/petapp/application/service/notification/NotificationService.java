package com.example.petapp.application.service.notification;

import com.example.petapp.application.in.chatting.model.dto.NotificationDto;
import com.example.petapp.application.in.chatting.model.dto.SendResponseDto;
import com.example.petapp.application.in.chatting.model.type.CommandType;
import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.application.in.notification.NotificationUseCase;
import com.example.petapp.application.in.notification.dto.NotificationEvent;
import com.example.petapp.application.in.notification.dto.NotificationListDto;
import com.example.petapp.application.out.SendPort;
import com.example.petapp.application.out.cache.AppOnlineCachePort;
import com.example.petapp.application.out.cache.NotificationsCachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService implements NotificationUseCase {

    private final NotificationsCachePort notificationsCachePort;
    private final AppOnlineCachePort appOnlineCachePort;
    private final MemberQueryUseCase useCase;
    private final SendPort sendPort;
    //    private final FcmService fcmService;


    @Transactional(readOnly = true)
    @Override
    public List<NotificationListDto> getList(Long id) {//몇분 전 추가해야할듯.
        return notificationsCachePort.getList(useCase.findOrThrow(id).getId());
    }

    /*
     * foreground 유저는 sse, background 유저는 fcm --x
     * foreground 유저는 stomp, background 유저는 fcm
     * */
    @Override
    public void send(NotificationEvent event) {
        if (appOnlineCachePort.exist(event.id())) {
            sendPort.send("/sub/notification/" + event.id(),
                    SendResponseDto.builder().commandType(CommandType.NOTIFICATION).body(new NotificationDto(event.id(), event.message())).build());
        } else {
            log.info("backGroundMember");
//            fcmService.sendNotification(member.getFcmToken().getFcmToken(), "명냥로드", message);
        }
        notificationsCachePort.create(event.id(), new NotificationListDto(event.message(), LocalDateTime.now()), 3);
    }
}


