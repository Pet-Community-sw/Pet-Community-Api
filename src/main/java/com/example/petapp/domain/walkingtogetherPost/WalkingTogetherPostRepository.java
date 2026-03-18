package com.example.petapp.domain.walkingtogetherPost;

import com.example.petapp.domain.post.model.RecommendRoutePost;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WalkingTogetherPostRepository {

    List<WalkingTogetherPost> findAllByRecommendRoutePost(RecommendRoutePost recommendRoutePost);

    List<WalkingTogetherPost> findAllByProfileContainsAndScheduledTimeBetween(Profile profile, LocalDateTime startDateTime, LocalDateTime endDateTime);

    WalkingTogetherPost save(WalkingTogetherPost walkingTogetherPost);

    void delete(WalkingTogetherPost walkingTogetherPost);

    Optional<WalkingTogetherPost> find(Long id);
}
