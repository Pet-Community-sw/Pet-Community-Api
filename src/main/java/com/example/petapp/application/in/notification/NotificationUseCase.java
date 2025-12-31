package com.example.petapp.application.in.notification;

import com.example.petapp.application.in.notification.dto.NotificationListDto;

import java.util.List;

public interface NotificationUseCase {

    List<NotificationListDto> getList(Long id);

//    SseEmitter subscribe(String token);
//
//    void sendNotification(Long id, String message);
}
