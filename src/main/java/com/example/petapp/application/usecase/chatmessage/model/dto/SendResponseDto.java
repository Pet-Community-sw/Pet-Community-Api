package com.example.petapp.application.usecase.chatmessage.model.dto;

import com.example.petapp.application.usecase.chatmessage.model.type.CommandType;
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
