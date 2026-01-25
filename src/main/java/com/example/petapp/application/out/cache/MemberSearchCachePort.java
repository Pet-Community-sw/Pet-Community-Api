package com.example.petapp.application.out.cache;

import com.example.petapp.application.in.member.object.dto.response.MemberSearchResponseDto;

import java.util.List;

public interface MemberSearchCachePort {
    void create(String key, int page, List<MemberSearchResponseDto> dtos);

    List<MemberSearchResponseDto> get(String key, int page);
}
