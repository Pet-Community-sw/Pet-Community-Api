package com.example.petapp.domain.chatting;

import com.example.petapp.domain.chatting.model.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChattingController {
    private final ChattingService chattingService;

    @MessageMapping("/chat")
    public void sendToMessage(@Payload ChatMessageDto chatMessage, Principal principal) {//memberId or profileId
        chattingService.sendToMessage(chatMessage, Long.valueOf(principal.getName()));
    }
}

