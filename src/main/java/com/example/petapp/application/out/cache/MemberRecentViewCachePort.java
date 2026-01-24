package com.example.petapp.application.out.cache;

import java.util.List;

public interface MemberRecentViewCachePort {
    void create(Long memberId, Long targetId);

    List<Long> findList(Long memberId);
}
