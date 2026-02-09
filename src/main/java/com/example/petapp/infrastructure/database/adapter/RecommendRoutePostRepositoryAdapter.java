package com.example.petapp.infrastructure.database.adapter;

import com.example.petapp.domain.post.RecommendRoutePostRepository;
import com.example.petapp.domain.post.model.RecommendRoutePost;
import com.example.petapp.infrastructure.database.jpa.post.JpaRecommendRoutePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RecommendRoutePostRepositoryAdapter implements RecommendRoutePostRepository {

    private final JpaRecommendRoutePostRepository repository;

    @Override
    public Page<RecommendRoutePost> findList(Double longitude, Double latitude, Pageable pageable) {
        return repository.findByRecommendRoutePostByPlace(longitude, latitude, pageable);
    }

    @Override
    public Page<RecommendRoutePost> findList(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, Pageable pageable) {
        return repository.findByRecommendRoutePostByLocation(minLongitude, minLatitude, maxLongitude, maxLatitude, pageable);
    }
}
