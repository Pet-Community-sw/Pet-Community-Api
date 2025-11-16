package com.example.petapp.domain.chatting.strategy.impl;

import com.example.petapp.domain.chatting.model.ChatMessage;
import com.example.petapp.domain.chatting.model.dto.NotificationDto;
import com.example.petapp.domain.chatting.model.dto.StompResponseDto;
import com.example.petapp.domain.chatting.model.type.CommandType;
import com.example.petapp.domain.chatting.strategy.MessageTypeStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnterStrategy implements MessageTypeStrategy {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void handle(ChatMessage chatMessage) {
        String message = (chatMessage.getSenderName() + "님이 입장하셨습니다.");
        NotificationDto notificationDto = new NotificationDto(chatMessage.getSenderId(), message);
        simpMessagingTemplate.convertAndSend("/sub/chat/" + chatMessage.getChatRoomId(),
                StompResponseDto.builder().commandType(CommandType.ENTER).body(notificationDto).build());
    }
}
