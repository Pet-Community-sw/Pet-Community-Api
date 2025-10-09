package com.example.PetApp.domain.chatting.strategy.impl;

import com.example.PetApp.domain.chatting.model.dto.EventMessageDto;
import com.example.PetApp.domain.chatting.model.dto.MessageResponseDto;
import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import com.example.PetApp.domain.chatting.model.type.MessageType;
import com.example.PetApp.domain.chatting.strategy.MessageTypeStrategy;
import com.example.PetApp.domain.groupchatroom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LeaveStrategy implements MessageTypeStrategy {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatRoomService chatRoomService;

    @Override
    public void handle(ChatMessage chatMessage) {
        String message = chatMessage.getSenderName() + "님이 나가셨습니다.";
        EventMessageDto eventMessageDto = new EventMessageDto(chatMessage.getSenderId(), message);
        simpMessagingTemplate.convertAndSend("/sub/chat/" + chatMessage.getChatRoomId(),
                MessageResponseDto.builder().messageType(MessageType.LEAVE).body(eventMessageDto).build());
        chatRoomService.deleteChatRoom(chatMessage.getChatRoomId(), chatMessage.getSenderId());
    }
}
