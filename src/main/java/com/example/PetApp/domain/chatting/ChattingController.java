package com.example.PetApp.domain.chatting;

import com.example.PetApp.domain.chatting.model.dto.ChatMessageDto;
import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChattingController {
    private final ChattingService chattingService;

    @MessageMapping("/chat/message")
    public void message(@Payload ChatMessageDto chatMessage, Principal principal) {//memberId or profileId
        chattingService.sendToMessage(chatMessage, Long.valueOf(principal.getName()));
    }
}

