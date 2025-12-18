package com.example.petapp.application.out;

import com.example.petapp.application.in.token.MemberInfo;
import com.example.petapp.domain.token.model.TokenType;

import java.util.List;

public interface TokenPort {

    String create(TokenType tokenType, Long memberId, Long profileId, String email, List<String> roles);

    MemberInfo getInfo(TokenType tokenType, String token);
}
