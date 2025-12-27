package com.example.petapp.application.in.chatting.model.dto;

import com.example.petapp.application.in.chatting.model.type.CommandType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatMessageDto {

    private CommandType commandType;
    private String clientMessageId; //C로부터 messageId를 받아 이걸로 어떤 메세지인지 판단
    private Long chatRoomId;
    private String message;
    private int seq;

}
