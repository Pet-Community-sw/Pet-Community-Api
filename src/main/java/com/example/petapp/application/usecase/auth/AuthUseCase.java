package com.example.petapp.application.usecase.auth;

import com.example.petapp.application.usecase.member.object.dto.request.AccessTokenResponseDto;
import com.example.petapp.application.usecase.member.object.dto.request.AuthCodeDto;
import com.example.petapp.application.usecase.member.object.dto.request.LoginDto;
import com.example.petapp.application.usecase.member.object.dto.request.SendEmailDto;
import com.example.petapp.application.usecase.member.object.dto.response.LoginResponseDto;

public interface AuthUseCase {

    LoginResponseDto login(LoginDto loginDto);

    void sendEmail(SendEmailDto sendEmailDto);

    void logout(String accessToken);

    AccessTokenResponseDto verifyCode(AuthCodeDto authCodeDto);
}
