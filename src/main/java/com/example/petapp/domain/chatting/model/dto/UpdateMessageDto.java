package com.example.petapp.domain.chatting.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateMessageDto {

    private int startSeq;
    private int endSeq;
}
