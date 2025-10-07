package com.example.PetApp.domain.chatting.model.dto;

import com.example.PetApp.domain.chatting.model.type.ChatRoomType;
import com.example.PetApp.domain.chatting.model.type.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatMessageDto {

    private ChatRoomType chatRoomType;
    private MessageType messageType;
    private Long chatRoomId;
    private String message;
    private Long seq;
}
