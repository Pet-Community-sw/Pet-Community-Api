package com.example.PetApp.domain.groupchatroom.model.dto.response;

import com.example.PetApp.domain.groupchatroom.model.dto.request.ChatMessageDtoMember;
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
