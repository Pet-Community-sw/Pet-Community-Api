package com.example.petapp.domain.query;

import com.example.petapp.domain.review.model.entity.Review;
import com.example.petapp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import com.example.petapp.domain.walkrecord.model.entity.WalkRecord;

public interface QueryService {
    
    Review findByReview(Long reviewId);

    WalkingTogetherMatch findByWalkingTogetherPost(Long walkingTogetherPostId);

    WalkRecord findByWalkRecord(Long walkRecordId);
}
