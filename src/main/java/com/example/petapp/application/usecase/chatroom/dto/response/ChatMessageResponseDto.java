package com.example.petapp.application.usecase.chatroom.dto.response;

import com.example.petapp.application.usecase.chatroom.dto.request.ChatMessageDtoMember;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponseDto {

    private Long chatRoomId;

    private List<ChatMessageDtoMember> messages;
}
