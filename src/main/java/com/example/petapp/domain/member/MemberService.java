package com.example.petapp.domain.member;

import com.example.petapp.domain.member.model.dto.request.*;
import com.example.petapp.domain.member.model.dto.response.FindByIdResponseDto;
import com.example.petapp.domain.member.model.dto.response.GetMemberResponseDto;
import com.example.petapp.domain.member.model.dto.response.MemberSignResponseDto;
import com.example.petapp.domain.member.model.dto.response.TokenResponseDto;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public interface MemberService {
    MemberSignResponseDto createMember(MemberSignDto memberSignDto);

    TokenResponseDto login(LoginDto loginDto, HttpServletResponse response);

    FindByIdResponseDto findById(String phoneNumber);

    void sendEmail(SendEmailDto sendEmailDto);

    void logout(String accessToken);

    AccessTokenResponseDto verifyCode(String email, String code);

    void resetPassword(ResetPasswordDto resetPasswordDto, String email);

    GetMemberResponseDto getMember(Long memberId, String email);

    void deleteMember(String email);

    void createFcmToken(FcmTokenDto fcmTokenDto);
}
