package com.example.petapp.application.in.match;

import com.example.petapp.domain.walkingtogethermatch.model.WalkingTogetherMatch;

public interface WalkingTogetherMatchQueryUseCase {
    WalkingTogetherMatch findOrThrow(Long id);
}
