package com.example.petapp.application.in.member.object.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MemberSearchResponseDto {
    private Long memberId;
    private String memberName;
    private String memberImageUrl;
}
