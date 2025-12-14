package com.example.petapp.application.out;

import com.example.petapp.domain.token.model.TokenType;

import java.util.List;

public interface TokenPort {

    String createAccessToken(Long memberId, Long profileId, String email, List<String> roles);

    String createRefreshToken(Long memberId, String email, List<String> roles);

    boolean isExpired(TokenType tokenType, String refreshToken);
}
