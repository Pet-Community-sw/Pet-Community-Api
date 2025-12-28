package com.example.petapp.infrastructure.stomp;

import com.example.petapp.application.in.chatting.model.dto.SendResponseDto;
import com.example.petapp.application.out.SendPort;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompAdapter implements SendPort {

    private final SimpMessagingTemplate template;

    @Override
    public void send(String destination, SendResponseDto<?> sendResponseDto) {
        template.convertAndSend(destination, sendResponseDto);
    }

    @Override
    public void sendToUser(String userId, String destination, SendResponseDto<?> sendResponseDto) {
        template.convertAndSendToUser(userId, destination, sendResponseDto);
    }
}
