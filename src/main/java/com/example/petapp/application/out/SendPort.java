package com.example.petapp.application.out;

import com.example.petapp.application.in.chatting.model.dto.StompResponseDto;

public interface SendPort {
    void send(String destination, StompResponseDto<?> stompResponseDto);

    void sendToUser(String userId, String destination, StompResponseDto<?> stompResponseDto);
}
