package com.example.petapp.common.jwt;

import com.example.petapp.application.out.TokenPort;
import com.example.petapp.domain.token.model.TokenType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtTokenAdapter implements TokenPort {

    
    @Override
    public String createAccessToken(Long memberId, Long profileId, String email, List<String> roles) {
        return "";
    }

    @Override
    public String createRefreshToken(Long memberId, String email, List<String> roles) {
        return "";
    }

    @Override
    public boolean isExpired(TokenType tokenType, String refreshToken) {
        return false;
    }
}
