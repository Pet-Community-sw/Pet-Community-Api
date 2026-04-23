package com.example.petapp.application.out.cache;

import com.example.petapp.application.in.member.object.dto.response.MemberSearchResponseDto;

import java.util.List;

public interface MemberSearchSuggestionsCachePort {

    List<MemberSearchResponseDto> get(String keyword);

    void create(String keyword, List<MemberSearchResponseDto> memberSearchResponseDtos);

}
