package com.example.petapp.infrastructure.database.jpa.match;

import com.example.petapp.domain.post.model.RecommendRoutePost;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaWalkingTogetherPostRepository extends JpaRepository<WalkingTogetherPost, Long> {
    List<WalkingTogetherPost> findAllByRecommendRoutePost(RecommendRoutePost recommendRoutePost);

    List<WalkingTogetherPost> findAllByProfileContainsAndScheduledTimeBetween(Profile profile, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
