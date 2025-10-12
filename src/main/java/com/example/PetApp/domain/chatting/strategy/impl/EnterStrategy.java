package com.example.PetApp.domain.chatting.strategy.impl;

import com.example.PetApp.domain.chatting.model.dto.EventMessageDto;
import com.example.PetApp.domain.chatting.model.dto.MessageResponseDto;
import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import com.example.PetApp.domain.chatting.model.type.MessageType;
import com.example.PetApp.domain.chatting.strategy.MessageTypeStrategy;
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
        EventMessageDto eventMessageDto = new EventMessageDto(chatMessage.getSenderId(), message);
        simpMessagingTemplate.convertAndSend("/sub/chat/" + chatMessage.getChatRoomId(),
                MessageResponseDto.builder().messageType(MessageType.ENTER).body(eventMessageDto).build());
    }
}
