package com.example.petapp.application.in.member.mapper;

import com.example.petapp.application.common.NameChosungUtil;
import com.example.petapp.application.in.member.object.MemberEvent;
import com.example.petapp.application.in.member.object.dto.request.MemberSignDto;
import com.example.petapp.application.in.member.object.dto.response.GetMemberResponseDto;
import com.example.petapp.application.in.member.object.dto.response.LoginResponseDto;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.member.model.MemberSearch;

public class MemberMapper {

    public static Member toEntity(MemberSignDto memberSignDto, String encodedPassword, String imageFileName) {
        return Member.builder()
                .name(memberSignDto.getName())
                .nameChosung(NameChosungUtil.getChosung(memberSignDto.getName()))
                .email(memberSignDto.getEmail())
                .password(encodedPassword)
                .phoneNumber(memberSignDto.getPhoneNumber())
                .memberImageUrl(imageFileName)
                .build();
    }

    public static MemberSearch toSearchEntity(MemberEvent event, Long outboxId) {
        return MemberSearch.builder()
                .memberId(event.getMemberId())
                .memberName(event.getMemberName())
                .memberNameChosung(event.getMemberNameChosung())
                .memberImageUrl(event.getMemberImageUrl())
                .outboxEventId(outboxId)
                .build();
    }

    public static MemberSearch toDeleteSearchEntity(MemberEvent event, Long outboxId) {
        return MemberSearch.builder()
                .memberId(event.getMemberId())
                .outboxEventId(outboxId)
                .isDeleted(true)
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
