package com.example.petapp.application.out.cache;

import com.example.petapp.application.in.member.object.dto.response.MemberSearchResponseDto;

import java.time.Duration;
import java.util.List;

public interface MemberSearchCachePort {

    List<MemberSearchResponseDto> get(String keyword);

    void set(String keyword, List<MemberSearchResponseDto> memberSearchResponseDtos, Duration duration);

}
