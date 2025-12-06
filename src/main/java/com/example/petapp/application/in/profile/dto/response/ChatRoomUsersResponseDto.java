package com.example.petapp.application.in.profile.dto.response;

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
