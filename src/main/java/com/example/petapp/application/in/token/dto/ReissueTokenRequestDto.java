package com.example.petapp.application.in.token.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReissueTokenRequestDto {
    private String refreshToken;
}
