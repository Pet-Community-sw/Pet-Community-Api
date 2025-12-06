package com.example.petapp.infrastructure.database.jpa.post;

import com.example.petapp.domain.post.model.RecommendRoutePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaRecommendRoutePostRepository extends JpaRepository<RecommendRoutePost, Long> {
    @Query("select r from RecommendRoutePost r " +
            "where st_distance_sphere(point(r.location.locationLongitude, r.location.locationLatitude), point(:longitude, :latitude)) <= 1000" +
            "order by r.createdAt desc")
    Page<RecommendRoutePost> findByRecommendRoutePostByPlace(@Param("longitude") Double longitude,
                                                             @Param("latitude") Double latitude,
                                                             Pageable pageable);

    @Query("select r from RecommendRoutePost r where r.location.locationLongitude between :minLongitude and :maxLongitude " +
            "and r.location.locationLatitude between :minLatitude and :maxLatitude " +
            "order by r.createdAt desc ")
    Page<RecommendRoutePost> findByRecommendRoutePostByLocation(
            @Param("minLongitude") Double minLongitude,
            @Param("minLatitude") Double minLatitude,
            @Param("maxLongitude") Double maxLongitude,
            @Param("maxLatitude") Double maxLatitude,
            Pageable pageable
    );
}
