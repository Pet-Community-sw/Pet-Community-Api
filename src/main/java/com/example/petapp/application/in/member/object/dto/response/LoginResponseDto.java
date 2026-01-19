package com.example.petapp.application.in.member.object.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    private String accessToken;

    private String refreshToken;

    private Long memberId;
}
