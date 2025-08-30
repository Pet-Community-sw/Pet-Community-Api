package com.example.PetApp.domain.profile.model.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomProfilesResponseDto {

    private Long profileId;

    private String profileImageUrl;
}
