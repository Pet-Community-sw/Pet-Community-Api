package com.example.petapp.domain.token;

import com.example.petapp.domain.member.model.dto.request.AccessTokenResponseDto;
import com.example.petapp.domain.member.model.dto.response.LoginResponseDto;
import com.example.petapp.domain.member.model.dto.response.TokenResponseDto;
import com.example.petapp.domain.member.model.entity.Member;
import org.springframework.stereotype.Service;

@Service
public interface TokenService {
    void deleteRefreshToken(String email);

    LoginResponseDto save(Member member);

    AccessTokenResponseDto createResetPasswordJwt(String email);

    String newAccessTokenByProfile(String accessToken, Member member, Long profileId);

    TokenResponseDto reissueToken(String refreshToken);
}
