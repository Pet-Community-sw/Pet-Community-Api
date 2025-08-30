package com.example.PetApp.domain.groupchatroom.model.dto.response;

import com.example.PetApp.domain.profile.model.dto.response.ChatRoomProfilesResponseDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomsResponseDto {

    private Long chatRoomId;

    private String chatName;

    private int chatLimitCount;

    private int currentCount;

    private LocalDateTime chatRoomTime;

    private Set<ChatRoomProfilesResponseDto> profiles = new HashSet<>();

    private String lastMessage;

    private int unReadCount;

    private LocalDateTime lastMessageTime;

    private boolean isOwner;


}
