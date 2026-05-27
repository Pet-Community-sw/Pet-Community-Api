package com.example.petapp.application.usecase.member;

import com.example.petapp.application.usecase.member.object.dto.response.MemberSearchResponseDto;

import java.util.List;

public interface MemberSearchUseCase {
    List<MemberSearchResponseDto> searchSuggestions(String keyword, Long memberId);

    List<MemberSearchResponseDto> searchMembers(String keyword, int page, Long memberId);
}
