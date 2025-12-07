package com.example.petapp.domain.query;

import com.example.petapp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;

public interface QueryService {

    WalkingTogetherMatch findByWalkingTogetherPost(Long walkingTogetherPostId);
}
