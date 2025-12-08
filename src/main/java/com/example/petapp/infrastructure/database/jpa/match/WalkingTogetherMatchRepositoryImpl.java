package com.example.petapp.infrastructure.database.jpa.match;

import com.example.petapp.domain.post.model.RecommendRoutePost;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.walkingtogethermatch.WalkingTogetherMatchRepository;
import com.example.petapp.domain.walkingtogethermatch.model.WalkingTogetherMatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WalkingTogetherMatchRepositoryImpl implements WalkingTogetherMatchRepository {

    private final JpaWalkingTogetherMatchRepository repository;

    @Override
    public List<WalkingTogetherMatch> findAllByRecommendRoutePost(RecommendRoutePost recommendRoutePost) {
        return repository.findAllByRecommendRoutePost(recommendRoutePost);
    }

    @Override
    public List<WalkingTogetherMatch> findAllByProfileContainsAndScheduledTimeBetween(Profile profile, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return repository.findAllByProfileContainsAndScheduledTimeBetween(profile, startDateTime, endDateTime);
    }

    @Override
    public WalkingTogetherMatch save(WalkingTogetherMatch walkingTogetherMatch) {
        return repository.save(walkingTogetherMatch);
    }

    @Override
    public void delete(WalkingTogetherMatch walkingTogetherMatch) {
        repository.delete(walkingTogetherMatch);
    }

    @Override
    public Optional<WalkingTogetherMatch> find(Long id) {
        return repository.findById(id);
    }
}
