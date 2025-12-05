package com.example.petapp.domain.notification;

import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.domain.notification.model.dto.NotificationListDto;
import com.example.petapp.port.InMemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final InMemoryService inMemoryService;
    private final MemberQueryUseCase useCase;

    @Transactional(readOnly = true)
    @Override
    public List<NotificationListDto> getNotifications(String email) {//몇분 전 추가해야할듯.
        return inMemoryService.getNotifications(useCase.findOrThrow(email).getId());
    }

//    @Override
//    public SseEmitter subscribe(String token) {
//        return sseEmitterManager.subscribe(token);
//    }
//
//    @Override
//    public void sendNotification(Long memberId, String message) {
//        sseEmitterManager.sendNotification(memberId, message);
}


