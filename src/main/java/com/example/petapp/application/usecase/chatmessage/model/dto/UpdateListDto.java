package com.example.petapp.application.usecase.chatmessage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class UpdateListDto {

    private Long chatRoomId;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
}
