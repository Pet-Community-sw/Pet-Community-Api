package com.example.petapp.application.out.cache;

import java.util.List;

public interface MemberRecentViewCachePort {
    void createRecentView(Long memberId, Long targetMemberId);

    List<Long> findRecentViewMemberIds(Long memberId);
}
