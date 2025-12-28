package com.example.petapp.application.out;

import com.example.petapp.application.in.chatting.model.dto.SendResponseDto;

public interface SendPort {
    void send(String destination, SendResponseDto<?> sendResponseDto);

    void sendToUser(String userId, String destination, SendResponseDto<?> sendResponseDto);
}
