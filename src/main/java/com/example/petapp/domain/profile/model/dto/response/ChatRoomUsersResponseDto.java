package com.example.petapp.domain.profile.model.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomUsersResponseDto {

    private Long userId;

    private String userImageUrl;
}
