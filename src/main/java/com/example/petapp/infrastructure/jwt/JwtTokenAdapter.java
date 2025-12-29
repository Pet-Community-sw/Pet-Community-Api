package com.example.petapp.infrastructure.jwt;

import com.example.petapp.application.in.token.MemberInfo;
import com.example.petapp.application.out.TokenPort;
import com.example.petapp.domain.token.model.TokenType;
import com.example.petapp.infrastructure.jwt.util.JwtTokenizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenAdapter implements TokenPort {

    private final JwtTokenizer tokenizer;

    @Override
    public String create(TokenType tokenType, Long memberId, Long profileId, String name, List<String> roles) {
        return tokenizer.create(tokenType, memberId, profileId, name, roles);
    }

    @Override
    public MemberInfo getInfo(TokenType tokenType, String token) {
        return tokenizer.getInfo(tokenType, token);
    }
}
