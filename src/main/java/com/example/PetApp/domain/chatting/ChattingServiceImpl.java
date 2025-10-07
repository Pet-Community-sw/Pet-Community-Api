package com.example.PetApp.domain.chatting;

import com.example.PetApp.domain.chatting.handler.ChatMessageHandler;
import com.example.PetApp.domain.chatting.handler.ChatRoomHandler;
import com.example.PetApp.domain.chatting.model.dto.ChatMessageDto;
import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChattingServiceImpl implements ChattingService {

    private final ChatMessageHandler chatMessageHandler;
    private final ChatRoomHandler chatRoomHandler;

    @Override
    public void sendToMessage(ChatMessageDto chatMessageDto, Long senderId) {
        log.info("[STOMP] messageMapping 시작 chatRoomType: {}, messageType: {}", chatMessageDto.getChatRoomType(), chatMessageDto.getMessageType());

        ChatMessage chatMessage;
        switch (chatMessageDto.getChatRoomType()) {
            case MANY -> chatMessage = chatRoomHandler.handleGroupChat(chatMessageDto, senderId);
            case ONE -> chatMessage = chatRoomHandler.handleOneToOneChat(chatMessageDto, senderId);
            default -> {
                throw new IllegalArgumentException("지원하지 않는 chatRoomType입니다.");
            }
        }

        switch (chatMessageDto.getMessageType()) {
            case ENTER -> chatMessageHandler.handleEnterMessage(chatMessage);
            case LEAVE -> chatMessageHandler.handleLeaveMessage(chatMessage, senderId);
            case TALK -> chatMessageHandler.handleTalkMessage(chatMessage);
            case READ -> chatMessageHandler.handleReadMessage(chatMessageDto, senderId);
            default -> {
                throw new IllegalArgumentException("지원하지 않는 chatMessageType입니다.");
            }
        }

    }
}
