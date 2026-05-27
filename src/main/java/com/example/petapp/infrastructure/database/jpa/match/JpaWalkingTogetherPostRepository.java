package com.example.petapp.infrastructure.database.jpa.match;

import com.example.petapp.domain.post.model.RecommendRoutePost;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaWalkingTogetherPostRepository extends JpaRepository<WalkingTogetherPost, Long> {
    List<WalkingTogetherPost> findAllByRecommendRoutePost(RecommendRoutePost recommendRoutePost);

}
