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

    AccessTokenResponseDto verifyCode(AuthCodeDto authCodeDto);

    void resetPassword(ResetPasswordDto resetPasswordDto, Long memberId);

    GetMemberResponseDto getMember(Long targetId, Long memberId);

    void deleteMember(Long memberId);

    void createFcmToken(FcmTokenDto fcmTokenDto);
}
