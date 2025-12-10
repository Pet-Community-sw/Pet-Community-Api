package com.example.petapp.application.out.cache;

import com.example.petapp.domain.notification.model.dto.NotificationListDto;

public interface NotificationsCachePort {

    void create(Long key, NotificationListDto notificationListDto, int day);
}
