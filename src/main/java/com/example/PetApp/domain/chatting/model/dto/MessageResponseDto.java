package com.example.PetApp.domain.chatting.model.dto;

import com.example.PetApp.domain.chatting.model.type.MessageType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MessageResponseDto<T> {
    private MessageType messageType;
    private T body;
}
