package com.example.PetApp.domain.chatting.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class UpdateListDto {

    private Long chatRoomId;
    private int unReadCount;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
}
