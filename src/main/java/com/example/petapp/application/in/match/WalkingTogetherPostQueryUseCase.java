package com.example.petapp.application.in.match;

import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;

public interface WalkingTogetherPostQueryUseCase {
    WalkingTogetherPost findOrThrow(Long id);
}
