package com.example.petapp.domain.token.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReissueTokenRequestDto {
    private String refreshToken;
}
