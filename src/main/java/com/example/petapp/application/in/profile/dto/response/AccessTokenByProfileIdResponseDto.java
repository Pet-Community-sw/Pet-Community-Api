package com.example.petapp.application.in.profile.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessTokenByProfileIdResponseDto {
    private Long profileId;

    private String accessToken;

}
