package com.example.PetApp.domain.token;

import com.example.PetApp.domain.member.model.dto.request.AccessTokenResponseDto;
import com.example.PetApp.domain.member.model.dto.response.TokenResponseDto;
import com.example.PetApp.domain.member.model.entity.Member;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public interface TokenService {
    void deleteRefreshToken(String email);

    TokenResponseDto save(Member member, HttpServletResponse response);

    AccessTokenResponseDto createResetPasswordJwt(String email);

    String newAccessTokenByProfile(String accessToken, String refreshToken, Member member, Long profileId);

    TokenResponseDto reissueToken(String refreshToken);
}
