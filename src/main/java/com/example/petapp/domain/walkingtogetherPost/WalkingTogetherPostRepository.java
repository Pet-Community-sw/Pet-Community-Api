package com.example.petapp.domain.walkingtogetherPost;

import com.example.petapp.domain.post.model.RecommendRoutePost;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;

import java.util.List;
import java.util.Optional;

public interface WalkingTogetherPostRepository {

    List<WalkingTogetherPost> findAllByRecommendRoutePost(RecommendRoutePost recommendRoutePost);
    
    WalkingTogetherPost save(WalkingTogetherPost walkingTogetherPost);

    void delete(WalkingTogetherPost walkingTogetherPost);

    Optional<WalkingTogetherPost> find(Long id);
}
