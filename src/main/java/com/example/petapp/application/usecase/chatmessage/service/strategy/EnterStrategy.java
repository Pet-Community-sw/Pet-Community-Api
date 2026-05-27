package com.example.petapp.application.usecase.chatmessage.service.strategy;

import com.example.petapp.application.out.SendPort;
import com.example.petapp.application.usecase.chatmessage.MessageTypeStrategy;
import com.example.petapp.application.usecase.chatmessage.model.dto.NotificationDto;
import com.example.petapp.application.usecase.chatmessage.model.dto.SendResponseDto;
import com.example.petapp.application.usecase.chatmessage.model.type.CommandType;
import com.example.petapp.domain.chatmessage.model.ChatMessage;
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
