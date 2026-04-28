package com.example.petapp.application.in.member;

import com.example.petapp.application.in.member.object.dto.request.MemberSignDto;
import com.example.petapp.application.in.member.object.dto.request.ResetPasswordDto;
import com.example.petapp.application.in.member.object.dto.request.UpdateMemberRequestDto;
import com.example.petapp.application.in.member.object.dto.response.FindByIdResponseDto;
import com.example.petapp.application.in.member.object.dto.response.GetMemberResponseDto;
import com.example.petapp.application.in.member.object.dto.response.MemberSearchResponseDto;
import com.example.petapp.application.in.member.object.dto.response.MemberSignResponseDto;
import com.example.petapp.domain.member.model.Member;

import java.util.List;

public interface MemberUseCase {
    MemberSignResponseDto create(MemberSignDto memberSignDto);

    FindByIdResponseDto findById(String phoneNumber);

    void resetPassword(ResetPasswordDto resetPasswordDto, Long memberId);

    GetMemberResponseDto get(Long targetId, Long memberId);

    void delete(Long memberId);

//    void createFcmToken(FcmTokenDto fcmTokenDto);

    void update(UpdateMemberRequestDto requestDto, Long memberId);

    List<MemberSearchResponseDto> searchSuggestions(String keyword, Long memberId);

    List<MemberSearchResponseDto> searchMembers(String keyword, int page, Long memberId);

    Member findOrThrow(String email);

    Member findOrThrow(Long id);

    Member findOrThrowByPhoneNumber(String phoneNumber);

    List<String> findNamesOrThrowByIds(List<Long> ids);
}
