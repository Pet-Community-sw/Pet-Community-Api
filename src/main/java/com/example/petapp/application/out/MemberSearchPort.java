package com.example.petapp.application.out;

import com.example.petapp.application.in.member.object.dto.response.MemberSearchResponseDto;

import java.util.List;

public interface MemberSearchPort {
    List<MemberSearchResponseDto> searchSuggestions(String keyword);

    List<MemberSearchResponseDto> search(String keyword, int page);
}
