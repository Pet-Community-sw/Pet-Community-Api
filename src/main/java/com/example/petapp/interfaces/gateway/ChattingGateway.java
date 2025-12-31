package com.example.petapp.interfaces.gateway;

import com.example.petapp.application.in.chatting.ChattingUseCase;
import com.example.petapp.application.in.chatting.TypingIndicatorUseCase;
import com.example.petapp.application.in.chatting.model.dto.ChatMessageDto;
import com.example.petapp.application.in.chatting.model.dto.TypingMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChattingGateway {
    private final ChattingUseCase chattingUseCase;
    private final TypingIndicatorUseCase typingIndicatorUseCase;

    //일반 메시지 전송
    @MessageMapping("/chat")
    public void sendToMessage(@Payload ChatMessageDto chatMessage, Principal principal) {//id or profileId
        chattingUseCase.sendToMessage(chatMessage, Long.valueOf(principal.getName()));
    }

    //타이핑 상태 전송 ('입력 중...')
    @MessageMapping("/chat/typing")
    public void sendTypingMessage(@Payload TypingMessageDto typingMessageDto, Principal principal) {
        typingIndicatorUseCase.sendTypingStatus(typingMessageDto, Long.valueOf(principal.getName()));
    }
}

