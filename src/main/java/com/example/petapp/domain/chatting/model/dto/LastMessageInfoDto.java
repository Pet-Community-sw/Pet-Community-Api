package com.example.petapp.domain.chatting.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LastMessageInfoDto {

    private int lastSeq;
    private String lastMessage;
    private String lastMessageTime;
}
