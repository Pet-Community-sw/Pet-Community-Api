package com.example.petapp.application.in.member;

import com.example.petapp.application.in.member.object.dto.request.*;
import com.example.petapp.application.in.member.object.dto.response.*;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

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

    List<MemberSearchResponseDto> autoComplete(String keyword, int size);

    List<MemberSearchResponseDto> search(String keyword, int page, int size);
}
