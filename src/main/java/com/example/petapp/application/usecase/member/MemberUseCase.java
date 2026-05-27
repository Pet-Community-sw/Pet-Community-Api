package com.example.petapp.application.usecase.member;

import com.example.petapp.application.usecase.member.object.dto.request.MemberSignDto;
import com.example.petapp.application.usecase.member.object.dto.request.ResetPasswordDto;
import com.example.petapp.application.usecase.member.object.dto.request.UpdateMemberRequestDto;
import com.example.petapp.application.usecase.member.object.dto.response.FindByIdResponseDto;
import com.example.petapp.application.usecase.member.object.dto.response.GetMemberResponseDto;
import com.example.petapp.application.usecase.member.object.dto.response.MemberSignResponseDto;

public interface MemberUseCase {
    MemberSignResponseDto create(MemberSignDto memberSignDto);

    FindByIdResponseDto findById(String phoneNumber);

    void resetPassword(ResetPasswordDto resetPasswordDto, Long memberId);

    GetMemberResponseDto get(Long targetId, Long memberId);

    void delete(Long memberId);
    
    void update(UpdateMemberRequestDto requestDto, Long memberId);
}
