package com.example.petapp.domain.member.model.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessTokenResponseDto {
    private String newAccessToken;
}
