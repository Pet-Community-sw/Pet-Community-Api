package com.example.petapp.domain.member.mapper;

import com.example.petapp.domain.member.model.dto.request.MemberSignDto;
import com.example.petapp.domain.member.model.dto.response.GetMemberResponseDto;
import com.example.petapp.domain.member.model.dto.response.TokenResponseDto;
import com.example.petapp.domain.member.model.entity.Member;

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

    public static TokenResponseDto toLoginResponseDto(String refreshToken, String accessToken) {
        return TokenResponseDto.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .build();
    }

}
