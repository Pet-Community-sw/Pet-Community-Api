package com.example.petapp.application.in.member.mapper;

import com.example.petapp.application.in.member.dto.request.MemberSignDto;
import com.example.petapp.application.in.member.dto.response.GetMemberResponseDto;
import com.example.petapp.application.in.member.dto.response.LoginResponseDto;
import com.example.petapp.domain.member.model.Member;

public class MemberMapper {

    public static Member toEntity(MemberSignDto memberSignDto, String encodedPassword, String imageFileName) {
        return Member.builder()
                .name(memberSignDto.getName())
                .email(memberSignDto.getEmail())
                .password(encodedPassword)
                .phoneNumber(memberSignDto.getPhoneNumber())
                .memberImageUrl(imageFileName)
                .build();
    }

    public static GetMemberResponseDto toGetMemberResponseDto(Member member) {
        return GetMemberResponseDto.builder()
                .memberName(member.getName())
                .memberImageUrl(member.getMemberImageUrl())
                .build();
    }

    public static LoginResponseDto toLoginResponseDto(Member member, String refreshToken, String accessToken) {
        return LoginResponseDto.builder()
                .memberId(member.getId())
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .build();
    }
}
