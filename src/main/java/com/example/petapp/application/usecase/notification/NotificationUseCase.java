package com.example.petapp.application.usecase.notification;

import com.example.petapp.application.usecase.notification.dto.NotificationListDto;

import java.util.List;

public interface NotificationUseCase {

    List<NotificationListDto> getList(Long id);
}
