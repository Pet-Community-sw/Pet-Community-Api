package com.example.petapp.infrastructure.stomp;

import com.example.petapp.application.in.chatting.model.dto.StompResponseDto;
import com.example.petapp.application.out.SendPort;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompAdapter implements SendPort {

    private final SimpMessagingTemplate template;

    @Override
    public void send(String destination, StompResponseDto<?> stompResponseDto) {
        template.convertAndSend(destination, stompResponseDto);
    }

    @Override
    public void sendToUser(String userId, String destination, StompResponseDto<?> stompResponseDto) {
        template.convertAndSendToUser(userId, destination, stompResponseDto);
    }
}
