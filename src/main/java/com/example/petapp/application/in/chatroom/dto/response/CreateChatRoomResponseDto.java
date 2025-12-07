package com.example.petapp.application.in.chatroom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateChatRoomResponseDto {

    private Long chatRoomId;

    private boolean isCreated;
}
