package com.example.petapp.application.usecase.chatroom.dto.response;

import com.example.petapp.application.usecase.profile.dto.response.ChatRoomUsersResponseDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomResponseDto {

    private Long chatRoomId;

    private String chatName;

    @Builder.Default
    private Set<ChatRoomUsersResponseDto> users = new HashSet<>();

    private int userSize;

    private long unReadCount;

    private String lastMessage;

    private LocalDateTime lastMessageTime;

    private boolean isOwner;

}
