package com.example.petapp.interfaces.gateway;

import com.example.petapp.application.in.chatting.ChattingUseCase;
import com.example.petapp.application.in.chatting.model.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChattingGateway {
    private final ChattingUseCase chattingUseCase;

    @MessageMapping("/chat")
    public void sendToMessage(@Payload ChatMessageDto chatMessage, Principal principal) {//memberId or profileId
        chattingUseCase.sendToMessage(chatMessage, Long.valueOf(principal.getName()));
    }
}

