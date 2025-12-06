package com.example.petapp.domain.walkingtogethermatch;

import com.example.petapp.domain.post.recommend.model.entity.RecommendRoutePost;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WalkingTogetherMatchRepository extends JpaRepository<WalkingTogetherMatch, Long> {

    List<WalkingTogetherMatch> findAllByRecommendRoutePost(RecommendRoutePost recommendRoutePost);

    List<WalkingTogetherMatch> findAllByProfileContainsAndScheduledTimeBetween(Profile profile, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
