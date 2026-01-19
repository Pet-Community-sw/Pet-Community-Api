package com.example.petapp.application.in.member.object.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessTokenResponseDto {
    private String newAccessToken;
}
