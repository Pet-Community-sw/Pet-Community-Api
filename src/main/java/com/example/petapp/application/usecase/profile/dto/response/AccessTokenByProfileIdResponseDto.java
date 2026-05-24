package com.example.petapp.application.usecase.profile.dto.response;

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
