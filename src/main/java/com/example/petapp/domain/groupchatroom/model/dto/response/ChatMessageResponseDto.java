package com.example.petapp.domain.groupchatroom.model.dto.response;

import com.example.petapp.domain.groupchatroom.model.dto.request.ChatMessageDtoMember;
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
