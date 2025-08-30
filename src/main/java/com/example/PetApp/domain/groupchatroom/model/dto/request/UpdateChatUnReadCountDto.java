package com.example.PetApp.domain.groupchatroom.model.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateChatUnReadCountDto {

    private Long chatRoomId;
    private String id;
    private int chatUnReadCount;
}
