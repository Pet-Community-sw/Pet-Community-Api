package com.example.petapp.application.in.notification;

import com.example.petapp.application.in.notification.dto.NotificationListDto;
import com.example.petapp.domain.member.model.Member;

import java.util.List;

public interface NotificationUseCase {

    List<NotificationListDto> getList(Long id);

    void send(Member member, String message);

//    SseEmitter subscribe(String token);
//
//    void sendNotification(Long memberId, String message);
}
