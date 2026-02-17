package com.example.petapp.application.in.notification;

import com.example.petapp.application.in.notification.dto.NotificationListDto;
import com.example.petapp.infrastructure.mq.consumer.OutboxMessage;

import java.util.List;

public interface NotificationUseCase {

    List<NotificationListDto> getList(Long id);

    void send(OutboxMessage outboxMessage);
}
