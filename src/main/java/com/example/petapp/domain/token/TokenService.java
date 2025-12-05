package com.example.petapp.domain.token;

import com.example.petapp.application.in.member.dto.request.AccessTokenResponseDto;
import com.example.petapp.application.in.member.dto.response.LoginResponseDto;
import com.example.petapp.application.in.member.dto.response.TokenResponseDto;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.token.model.dto.request.ReissueTokenRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface TokenService {
    void deleteRefreshToken(String email);

    LoginResponseDto save(Member member);

    AccessTokenResponseDto createResetPasswordJwt(String email);

    String newAccessTokenByProfile(String accessToken, Member member, Long profileId);

    TokenResponseDto reissueToken(String accessToken, ReissueTokenRequestDto reissueTokenRequestDto);
}
