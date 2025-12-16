package com.example.petapp.common.jwt;

import com.example.petapp.application.out.TokenPort;
import com.example.petapp.domain.token.model.TokenType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtTokenAdapter implements TokenPort {


    @Override
    public String create(TokenType tokenType, Long memberId, Long profileId, String email, List<String> roles) {
        return "";
    }
}
