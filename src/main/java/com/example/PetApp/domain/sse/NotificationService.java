package com.example.PetApp.domain.sse;

import com.example.PetApp.domain.sse.model.dto.NotificationListDto;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Service
public interface NotificationService {

    List<NotificationListDto> getNotifications(String email);

    SseEmitter subscribe(String token);
}
