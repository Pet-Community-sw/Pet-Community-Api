package com.example.petapp.application.in.token;

import com.example.petapp.application.in.member.dto.request.AccessTokenResponseDto;
import com.example.petapp.application.in.member.dto.response.LoginResponseDto;
import com.example.petapp.application.in.member.dto.response.TokenResponseDto;
import com.example.petapp.application.in.token.dto.ReissueTokenRequestDto;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.role.Role;

public interface TokenUseCase {
    void delete(String authorization);

    LoginResponseDto save(Member member, Role role);

    AccessTokenResponseDto createResetPasswordJwt(String email);

    String newAccessTokenByProfile(String accessToken, Member member, Long profileId);

    TokenResponseDto reissueToken(String accessToken, ReissueTokenRequestDto reissueTokenRequestDto);
}
