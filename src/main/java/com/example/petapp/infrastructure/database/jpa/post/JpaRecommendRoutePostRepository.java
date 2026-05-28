package com.example.petapp.infrastructure.database.jpa.post;

import com.example.petapp.domain.post.model.RecommendRoutePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaRecommendRoutePostRepository extends JpaRepository<RecommendRoutePost, Long> {
    @Query(
            value = """
                    select r.*
                    from recommend_route_post r
                    join post p on p.id = r.post_id
                    where st_distance_sphere(
                        point(r.location_longitude, r.location_latitude),
                        point(:longitude, :latitude)
                    ) <= :radiusMeters
                    order by p.created_at desc
                    """,
            countQuery = """
                    select count(*)
                    from recommend_route_post r
                    where st_distance_sphere(
                        point(r.location_longitude, r.location_latitude),
                        point(:longitude, :latitude)
                    ) <= :radiusMeters
                    """,
            nativeQuery = true
    )
    Page<RecommendRoutePost> findByRecommendRoutePostByPlace(@Param("longitude") Double longitude,
                                                             @Param("latitude") Double latitude,
                                                             @Param("radiusMeters") int radiusMeters,
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
