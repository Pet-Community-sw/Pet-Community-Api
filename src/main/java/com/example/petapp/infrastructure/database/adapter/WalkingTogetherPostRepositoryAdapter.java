package com.example.petapp.infrastructure.database.adapter;

import com.example.petapp.domain.post.model.RecommendRoutePost;
import com.example.petapp.domain.walkingtogetherPost.WalkingTogetherPostRepository;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;
import com.example.petapp.infrastructure.database.jpa.match.JpaWalkingTogetherPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WalkingTogetherPostRepositoryAdapter implements WalkingTogetherPostRepository {

    private final JpaWalkingTogetherPostRepository repository;

    @Override
    public List<WalkingTogetherPost> findAllByRecommendRoutePost(RecommendRoutePost recommendRoutePost) {
        return repository.findAllByRecommendRoutePost(recommendRoutePost);
    }
    
    @Override
    public WalkingTogetherPost save(WalkingTogetherPost walkingTogetherPost) {
        return repository.save(walkingTogetherPost);
    }

    @Override
    public void delete(WalkingTogetherPost walkingTogetherPost) {
        repository.delete(walkingTogetherPost);
    }

    @Override
    public Optional<WalkingTogetherPost> find(Long id) {
        return repository.findById(id);
    }
}
