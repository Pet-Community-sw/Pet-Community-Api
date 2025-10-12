package com.example.PetApp.domain.groupchatroom.model.dto.response;

import com.example.PetApp.domain.profile.model.dto.response.ChatRoomUsersResponseDto;
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

    private int unReadCount;

    private String lastMessage;

    private LocalDateTime lastMessageTime;

    private boolean isOwner;

}
