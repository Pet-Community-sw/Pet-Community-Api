package com.example.petapp.domain.notification;

import com.example.petapp.domain.notification.model.dto.NotificationListDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationService {

    List<NotificationListDto> getNotifications(String email);

//    SseEmitter subscribe(String token);
//
//    void sendNotification(Long memberId, String message);
}
