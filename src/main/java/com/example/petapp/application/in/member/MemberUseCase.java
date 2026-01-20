package com.example.petapp.application.in.member;

import com.example.petapp.application.in.member.object.dto.request.*;
import com.example.petapp.application.in.member.object.dto.response.FindByIdResponseDto;
import com.example.petapp.application.in.member.object.dto.response.GetMemberResponseDto;
import com.example.petapp.application.in.member.object.dto.response.LoginResponseDto;
import com.example.petapp.application.in.member.object.dto.response.MemberSignResponseDto;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public interface MemberUseCase {
    MemberSignResponseDto create(MemberSignDto memberSignDto);

    LoginResponseDto login(LoginDto loginDto, HttpServletResponse response);

    FindByIdResponseDto findById(String phoneNumber);

    void sendEmail(SendEmailDto sendEmailDto);

    void logout(String accessToken);

    AccessTokenResponseDto verifyCode(AuthCodeDto authCodeDto);

    void resetPassword(ResetPasswordDto resetPasswordDto, Long memberId);

    GetMemberResponseDto get(Long targetId, Long memberId);

    void delete(Long memberId);

    void createFcmToken(FcmTokenDto fcmTokenDto);

    void update(UpdateMemberRequestDto requestDto, Long memberId);
}
