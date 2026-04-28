package com.example.petapp.application.in.token;

import com.example.petapp.application.in.member.object.dto.request.AccessTokenResponseDto;
import com.example.petapp.application.in.member.object.dto.response.LoginResponseDto;
import com.example.petapp.application.in.member.object.dto.response.TokenResponseDto;
import com.example.petapp.application.in.token.dto.ReissueTokenRequestDto;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.role.Role;
import com.example.petapp.domain.token.model.Token;

import java.util.Optional;

public interface TokenUseCase {
    void delete(String authorization);

    void delete(Long memberId);

    LoginResponseDto save(Member member, Role role);

    AccessTokenResponseDto createResetPasswordJwt(Member member);

    String newAccessTokenByProfile(String accessToken, Member member, Long profileId);

    TokenResponseDto reissueToken(String accessToken, ReissueTokenRequestDto reissueTokenRequestDto);

    Token findOrThrow(Long id);

    Optional<Token> find(Long id);
}
