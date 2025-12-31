package com.example.petapp.application.service.notification;

import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.application.in.notification.NotificationUseCase;
import com.example.petapp.application.in.notification.dto.NotificationListDto;
import com.example.petapp.application.out.cache.NotificationsCachePort;
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

    @Transactional(readOnly = true)
    @Override
    public List<NotificationListDto> getList(Long id) {//몇분 전 추가해야할듯.
        return notificationsCachePort.getList(useCase.findOrThrow(id).getId());
    }
}


