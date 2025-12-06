package com.example.petapp.domain.post;

import com.example.petapp.domain.post.model.RecommendRoutePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecommendRoutePostRepository {
    Page<RecommendRoutePost> findList(Double longitude, Double latitude, Pageable pageable);

    Page<RecommendRoutePost> findList(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, Pageable pageable);
}
