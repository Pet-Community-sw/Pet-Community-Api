package com.example.petapp.application.out.cache;

import com.example.petapp.application.usecase.member.object.dto.response.MemberSearchResponseDto;

import java.util.List;

public interface MemberSearchCachePort {
    List<MemberSearchResponseDto> findSuggestions(String keyword);

    void createSuggestions(String keyword, List<MemberSearchResponseDto> dtos);
}
