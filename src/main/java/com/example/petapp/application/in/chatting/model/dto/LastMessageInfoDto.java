package com.example.petapp.application.in.chatting.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LastMessageInfoDto {

    private Long lastSeq;
    private String lastMessage;
    private String lastMessageTime;
}
