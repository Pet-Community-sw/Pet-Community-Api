package com.example.petapp.application.out.cache;

import java.util.Set;

public interface LikeCachePort {
    Set<Long> findLikedMemberIds(Long postId);

    void createLike(Long postId, Long memberId);

    void deleteLike(Long postId, Long memberId);
}
