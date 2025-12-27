package com.example.petapp.application.in.chatting.model.dto;

import com.example.petapp.application.in.chatting.model.type.CommandType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StompResponseDto<T> {
    private CommandType commandType;
    private T body;
}
