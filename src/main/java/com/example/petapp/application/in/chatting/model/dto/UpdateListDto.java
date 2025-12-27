package com.example.petapp.application.in.chatting.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class UpdateListDto {

    private Long chatRoomId;
    private Long unReadCount;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
}
