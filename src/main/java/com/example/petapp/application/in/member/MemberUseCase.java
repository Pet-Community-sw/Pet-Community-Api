package com.example.petapp.application.in.member;

import com.example.petapp.application.in.member.dto.request.*;
import com.example.petapp.application.in.member.dto.response.FindByIdResponseDto;
import com.example.petapp.application.in.member.dto.response.GetMemberResponseDto;
import com.example.petapp.application.in.member.dto.response.LoginResponseDto;
import com.example.petapp.application.in.member.dto.response.MemberSignResponseDto;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public interface MemberUseCase {
    MemberSignResponseDto createMember(MemberSignDto memberSignDto);

    LoginResponseDto login(LoginDto loginDto, HttpServletResponse response);

    FindByIdResponseDto findById(String phoneNumber);

    void sendEmail(SendEmailDto sendEmailDto);

    void logout(String accessToken);

    AccessTokenResponseDto verifyCode(String email, String code);

    void resetPassword(ResetPasswordDto resetPasswordDto, String email);

    GetMemberResponseDto getMember(Long memberId, String email);

    void deleteMember(String email);

    void createFcmToken(FcmTokenDto fcmTokenDto);
}
