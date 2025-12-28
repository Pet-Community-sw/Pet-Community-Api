package com.example.petapp.application.service.chatting.strategy;

import com.example.petapp.application.in.chatting.MessageTypeStrategy;
import com.example.petapp.application.in.chatting.model.dto.NotificationDto;
import com.example.petapp.application.in.chatting.model.dto.SendResponseDto;
import com.example.petapp.application.in.chatting.model.type.CommandType;
import com.example.petapp.application.out.SendPort;
import com.example.petapp.domain.chatting.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnterStrategy implements MessageTypeStrategy {

    private final SendPort port;

    @Override
    public void handle(ChatMessage chatMessage) {
        String message = (chatMessage.getSenderName() + "님이 입장하셨습니다.");
        NotificationDto notificationDto = new NotificationDto(chatMessage.getSenderId(), message);
        port.send("/sub/chat/" + chatMessage.getChatRoomId(),
                SendResponseDto.builder().commandType(CommandType.ENTER).body(notificationDto).build());
    }

    @Override
    public CommandType getCommand() {
        return CommandType.ENTER;
    }
}
