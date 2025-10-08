package com.example.PetApp.common.stomp.strategy.command.impl;

import com.example.PetApp.common.stomp.strategy.command.StompCommandStrategy;
import com.example.PetApp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.PetApp.domain.query.QueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendStrategy implements StompCommandStrategy {

    private final QueryService queryService;

    @Override
    public void handle(StompHeaderAccessor accessor) {
        log.info("[STOMP] send 전략 시작");

        String destination = accessor.getDestination();
        Principal user = accessor.getUser();

        if (destination == null || user == null) {
            throw new IllegalArgumentException("destination 또는 user 정보가 없습니다.");
        }

        String chatRoomId = destination.substring("/pub/chat/".length());
        ChatRoom chatRoom = queryService.findByChatRoom(Long.valueOf(chatRoomId));
        chatRoom.validateUser(Long.valueOf(user.getName()));

    }
}
