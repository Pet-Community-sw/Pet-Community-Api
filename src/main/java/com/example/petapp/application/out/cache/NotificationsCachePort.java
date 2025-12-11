package com.example.petapp.application.out.cache;

import com.example.petapp.domain.notification.model.dto.NotificationListDto;

import java.util.List;

public interface NotificationsCachePort {

    void create(Long id, NotificationListDto notificationListDto, int day);

    List<NotificationListDto> getList(Long id);
}
