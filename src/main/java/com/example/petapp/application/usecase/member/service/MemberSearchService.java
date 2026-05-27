package com.example.petapp.application.usecase.member.service;

import com.example.petapp.application.out.MemberSearchPort;
import com.example.petapp.application.out.cache.MemberRecentViewCachePort;
import com.example.petapp.application.out.cache.MemberSearchCachePort;
import com.example.petapp.application.out.cache.MemberSearchSuggestionsCachePort;
import com.example.petapp.application.usecase.member.MemberSearchUseCase;
import com.example.petapp.application.usecase.member.object.dto.response.MemberSearchResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MemberSearchService implements MemberSearchUseCase {

    private final MemberSearchPort memberSearchPort;
    private final MemberSearchCachePort memberSearchCachePort;
    private final MemberSearchSuggestionsCachePort memberSearchSuggestionsCachePort;
    private final MemberRecentViewCachePort memberRecentViewCachePort;

    @Override
    public List<MemberSearchResponseDto> searchSuggestions(String keyword, Long memberId) {
        if (keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("키워드를 입력해주세요.");
        }

        String key = keywordFilter(keyword);
        List<MemberSearchResponseDto> result = memberSearchSuggestionsCachePort.get(key);

        if (result == null) {
            result = memberSearchPort.searchSuggestions(key);
            memberSearchSuggestionsCachePort.create(key, result);
        }
        if (result == null || result.isEmpty()) {
            return result;
        }

        return sortByRecentViews(result, memberId);
    }

    @Override
    public List<MemberSearchResponseDto> searchMembers(String keyword, int page, Long memberId) {
        if (keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("키워드를 입력해주세요.");
        }

        String key = keywordFilter(keyword);
        List<MemberSearchResponseDto> result = memberSearchCachePort.get(key, page);
        if (result == null) {
            result = memberSearchPort.search(key, page);
            memberSearchCachePort.create(key, page, result);
        }
        return result;
    }

    private List<MemberSearchResponseDto> sortByRecentViews(List<MemberSearchResponseDto> result, Long memberId) {
        List<Long> viewList = memberRecentViewCachePort.findList(memberId);
        if (viewList == null || viewList.isEmpty()) {
            return result;
        }

        Map<Long, MemberSearchResponseDto> memberMap = new HashMap<>(result.size());
        for (MemberSearchResponseDto dto : result) {
            memberMap.put(dto.getMemberId(), dto);
        }

        List<MemberSearchResponseDto> sorted = new ArrayList<>(result.size());
        Set<Long> picked = new HashSet<>();

        for (Long id : viewList) {
            MemberSearchResponseDto dto = memberMap.get(id);
            if (dto != null && picked.add(id)) {
                sorted.add(dto);
            }
        }

        for (MemberSearchResponseDto dto : result) {
            if (picked.add(dto.getMemberId())) {
                sorted.add(dto);
            }
        }

        return sorted;
    }

    private String keywordFilter(String keyword) {
        return keyword.replaceAll("\\s+", "").toLowerCase();
    }
}
