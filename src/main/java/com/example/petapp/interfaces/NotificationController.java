package com.example.petapp.interfaces;

import com.example.petapp.application.common.AuthUtil;
import com.example.petapp.application.in.notification.NotificationUseCase;
import com.example.petapp.application.in.notification.dto.NotificationListDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Notification")
@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationUseCase notificationUseCase;
//
//    sse는 브라우저 기반이므로 stomp로 알림 전송.
//    @Operation(
//            summary = "sse 구독 (스웨거에서 요청x) "
//    )
//    @GetMapping(value = "/subscribe", produces = "text/event-stream")//sse 서버에서 클라이언트로 실시간으로 보내기 위함.
//    public SseEmitter subscribe(@RequestParam String token) {
//        return notificationService.subscribe(token);
//    }

    @Operation(
            summary = "알림 목록 조회"
    )
    @GetMapping
    public List<NotificationListDto> getNotifications(Authentication authentication) {
        return notificationUseCase.getList(AuthUtil.getMemberId(authentication));
    }
}
