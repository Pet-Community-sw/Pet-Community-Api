package com.example.PetApp.domain.chatting;

import com.example.PetApp.domain.chatting.handler.ChatMessageHandler;
import com.example.PetApp.domain.chatting.handler.ChatRoomHandler;
import com.example.PetApp.domain.chatting.model.dto.ChatMessageDto;
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

        switch (chatMessageDto.getChatRoomType()) {
            case MANY -> chatRoomHandler.handleGroupChat(chatMessageDto, senderId);
            case ONE -> chatRoomHandler.handleOneToOneChat(chatMessageDto, senderId);
            default -> {
                throw new IllegalArgumentException("지원하지 않는 chatRoomType입니다.");
            }
        }

        switch (chatMessageDto.getMessageType()) {
            case ENTER -> chatMessageHandler.handleEnterMessage(chatMessageDto);
            case LEAVE -> chatMessageHandler.handleLeaveMessage(chatMessageDto, senderId);
            case TALK -> chatMessageHandler.handleTalkMessage(chatMessageDto);
            default -> {
                throw new IllegalArgumentException("지원하지 않는 chatMessageType입니다.");
            }
        }

    }
}
