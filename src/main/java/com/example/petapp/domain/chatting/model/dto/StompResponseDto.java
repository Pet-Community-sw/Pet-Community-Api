package com.example.petapp.domain.chatting.model.dto;

import com.example.petapp.domain.chatting.model.type.CommandType;
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
