package com.example.PetApp.domain.token;

import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.member.model.dto.request.AccessTokenResponseDto;
import com.example.PetApp.domain.member.model.dto.response.LoginResponseDto;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public interface TokenService {
    void deleteRefreshToken(String email);

    LoginResponseDto save(Member member, HttpServletResponse response);

    AccessTokenResponseDto reissueAccessToken(String accessToken, String refreshToken);


    AccessTokenResponseDto createResetPasswordJwt(String email);

    String newAccessTokenByProfile(String accessToken, String refreshToken, Member member, Long profileId);
}
