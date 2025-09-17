package com.example.PetApp.domain.memberchatRoom.model.dto.response;

import com.example.PetApp.common.util.notification.NotificationDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMemberChatRoomResponseDto {
    private Long memberChatRoomId;

    @JsonIgnore
    private NotificationDto notificationDto;
}
