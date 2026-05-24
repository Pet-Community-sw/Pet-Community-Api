package com.example.petapp.application.usecase.chatting.model.dto;

import com.example.petapp.application.usecase.chatting.model.type.CommandType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SendResponseDto<T> {
    private CommandType commandType;
    private T body;
}
