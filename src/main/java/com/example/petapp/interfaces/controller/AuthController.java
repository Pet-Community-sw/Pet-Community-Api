package com.example.petapp.interfaces.controller;

import com.example.petapp.application.in.auth.AuthUseCase;
import com.example.petapp.application.in.member.object.dto.request.AccessTokenResponseDto;
import com.example.petapp.application.in.member.object.dto.request.AuthCodeDto;
import com.example.petapp.application.in.member.object.dto.request.LoginDto;
import com.example.petapp.application.in.member.object.dto.request.SendEmailDto;
import com.example.petapp.application.in.member.object.dto.response.LoginResponseDto;
import com.example.petapp.interfaces.dto.MessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Auths")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @Operation(
            summary = "로그인"
    )
    @PostMapping
    public LoginResponseDto login(@RequestBody @Valid LoginDto loginDto) {
        return authUseCase.login(loginDto);
    }

    @Operation(
            summary = "로그아웃"
    )
    @DeleteMapping
    public ResponseEntity<MessageResponse> logout(@Parameter(hidden = true) @RequestHeader("Authorization") String accessToken) {
        authUseCase.logout(accessToken);
        return ResponseEntity.ok(new MessageResponse("로그아웃 되었습니다."));
    }

    @Operation(
            summary = "이메일 인증코드 요청"
    )
    @PostMapping("/emails")
    public ResponseEntity<MessageResponse> sendEmail(@RequestBody @Valid SendEmailDto sendEmailDto) {
        authUseCase.sendEmail(sendEmailDto);
        return ResponseEntity.ok(new MessageResponse("인증번호가 이메일로 전송되었습니다."));
    }

    @Operation(
            summary = "인증코드 검증"
    )
    @PostMapping("/emails/verify")
    public AccessTokenResponseDto verifyCode(@RequestBody @Valid AuthCodeDto authCodeDto) {
        return authUseCase.verifyCode(authCodeDto);
    }
}
