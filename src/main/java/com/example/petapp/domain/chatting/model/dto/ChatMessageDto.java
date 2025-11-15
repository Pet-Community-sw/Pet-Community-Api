package com.example.petapp.domain.chatting.model.dto;

import com.example.petapp.domain.chatting.model.type.CommandType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatMessageDto {

    private CommandType commandType;
    private Long chatRoomId;
    private String message;
    private int seq;

}
