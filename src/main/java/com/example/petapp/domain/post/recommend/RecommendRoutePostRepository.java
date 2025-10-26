package com.example.petapp.domain.post.recommend;

import com.example.petapp.domain.post.common.PostRepository;
import com.example.petapp.domain.post.recommend.model.entity.RecommendRoutePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendRoutePostRepository extends PostRepository<RecommendRoutePost> {

    /*
     * JPA가 기본적으로 value 쿼리를 기반으로 COUNT 쿼리를 자동 생성하려고 시도함.
     * JOIN, DISTINCT, GROUP BY 또는 거리 계산 함수 같은 게 들어가면 정확한 COUNT 쿼리를 자동 생성하지 못함 → 성능 저하 or 에러 발생 가능.
     * 그래서 명시적으로 Count 전용 쿼리를 지정해주는 게 안정적이고 빠름.
     * */
    @Query(value = """
            select r.*, p.* from recommend_route_post r left join post p on p.id = r.post_id
            where st_distance_sphere(point(r.location_longitude, r.location_latitude), point(:longitude, :latitude)) <= 1000
            order by p.created_at desc
            """,
            countQuery = """
                    select count(1)
                    from recommend_route_post r
                    left join  post p on p.id = r.post_id
                    where st_distance_sphere(point(r.location_longitude, r.location_latitude), point(:longitude, :latitude)) <= 1000
                    """, nativeQuery = true)
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
