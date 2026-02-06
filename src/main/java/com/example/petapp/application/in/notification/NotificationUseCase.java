package com.example.petapp.application.in.notification;

import com.example.petapp.application.in.notification.dto.NotificationListDto;
import com.example.petapp.domain.outboxevent.model.OutboxEvent;

import java.util.List;

public interface NotificationUseCase {

    List<NotificationListDto> getList(Long id);

    void send(OutboxEvent event);
}
