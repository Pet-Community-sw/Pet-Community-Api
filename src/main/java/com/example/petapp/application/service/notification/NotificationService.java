package com.example.petapp.application.service.notification;

import com.example.petapp.application.out.cache.NotificationsCachePort;
import com.example.petapp.application.usecase.member.MemberQueryUseCase;
import com.example.petapp.application.usecase.notification.NotificationUseCase;
import com.example.petapp.application.usecase.notification.dto.NotificationListDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService implements NotificationUseCase {

    private final NotificationsCachePort notificationsCachePort;
    private final MemberQueryUseCase useCase;
    //    private final FcmService fcmService;


    @Transactional(readOnly = true)
    @Override
    public List<NotificationListDto> getList(Long id) {
        return notificationsCachePort.getList(useCase.findOrThrow(id).getId());
    }
}


