package com.example.PetApp.domain.walkingtogethermatch;

import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.domain.post.recommend.model.entity.RecommendRoutePost;
import com.example.PetApp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WalkingTogetherMatchRepository extends JpaRepository<WalkingTogetherMatch, Long> {

    List<WalkingTogetherMatch> findAllByRecommendRoutePost(RecommendRoutePost recommendRoutePost);

    List<WalkingTogetherMatch> findAllByProfileContainsAndScheduledTimeBetween(Profile profile, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
