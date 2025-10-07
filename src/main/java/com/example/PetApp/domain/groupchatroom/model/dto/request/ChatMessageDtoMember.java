package com.example.PetApp.domain.groupchatroom.model.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDtoMember {

    private Long senderId;

    private String senderName;

    private String senderImageUrl;

    private String message;

    private int unReadCount;

    private LocalDateTime messageTime;

}
