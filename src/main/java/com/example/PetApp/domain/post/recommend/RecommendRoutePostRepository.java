package com.example.PetApp.domain.post.recommend;

import com.example.PetApp.domain.post.recommend.model.entity.RecommendRoutePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendRoutePostRepository extends JpaRepository<RecommendRoutePost, Long> {

    @Query(value = """
            SELECT p.*, r.*
            FROM recommend_route_post r
            LEFT JOIN post p ON p.id = r.post_id
            WHERE ST_Distance_Sphere(POINT(r.location_longitude, r.location_latitude), POINT(:longitude, :latitude)) <= 1000
            ORDER BY p.created_at DESC
            """,
            countQuery = """
                    SELECT COUNT(1)
                    FROM recommend_route_post r
                    LEFT JOIN post p ON p.id = r.post_id
                    WHERE ST_Distance_Sphere(POINT(r.location_longitude, r.location_latitude), POINT(:longitude, :latitude)) <= 1000
                    """,
            nativeQuery = true)
    Page<RecommendRoutePost> findByRecommendRoutePostByPlace(
            @Param("longitude") Double longitude,
            @Param("latitude") Double latitude,
            Pageable pageable
    );

    @Query(value = """
            SELECT p.*, r.*
            FROM recommend_route_post r
            LEFT JOIN post p ON p.id = r.post_id
            WHERE r.location_longitude BETWEEN :minLongitude AND :maxLongitude
              AND r.location_latitude  BETWEEN :minLatitude  AND :maxLatitude
            ORDER BY p.created_at DESC
            """,
            countQuery = """
                    SELECT COUNT(1)
                    FROM recommend_route_post r
                    LEFT JOIN post p ON p.id = r.post_id
                    WHERE r.location_longitude BETWEEN :minLongitude AND :maxLongitude
                      AND r.location_latitude  BETWEEN :minLatitude  AND :maxLatitude
                    """,
            nativeQuery = true)
    Page<RecommendRoutePost> findByRecommendRoutePostByLocation(
            @Param("minLongitude") Double minLongitude,
            @Param("minLatitude") Double minLatitude,
            @Param("maxLongitude") Double maxLongitude,
            @Param("maxLatitude") Double maxLatitude,
            Pageable pageable
    );
}
