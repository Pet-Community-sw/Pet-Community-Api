package com.example.petapp.domain.post.delegate;

import com.example.petapp.domain.post.delegate.model.entity.DelegateWalkPost;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DelegateWalkPostRepository extends JpaRepository<DelegateWalkPost, Long> {
    @Query(value = """
            select d.*, p.* from delegate_walk_post d left join post p on p.id = d.post_id
            where st_distance_sphere(point(d.location_longitude, d.location_latitude), point(:longitude, :latitude)) <= 1000
            order by p.created_at desc
            """,
            countQuery = """
                    select count(1)
                    from delegate_walk_post d
                    left join  post p on p.id = d.post_id
                    where st_distance_sphere(point(d.location_longitude, d.location_latitude), point(:longitude, :latitude)) <= 1000
                    """, nativeQuery = true)
    Page<DelegateWalkPost> findByDelegateWalkPostByPlace(//나중에 paging해야할듯.
                                                         @Param("longitude") Double longitude,
                                                         @Param("latitude") Double latitude,
                                                         Pageable pageable
    );

    @Query("select d from DelegateWalkPost d where d.location.locationLongitude between :minLongitude and :maxLongitude " +
            "and d.location.locationLatitude between :minLatitude and :maxLatitude " +
            "order by d.createdAt desc ")
    Page<DelegateWalkPost> findByDelegateWalkPostByLocation(
            @Param("minLongitude") Double minLongitude,
            @Param("minLatitude") Double minLatitude,
            @Param("maxLongitude") Double maxLongitude,
            @Param("maxLatitude") Double maxLatitude,
            Pageable pageable
    );


    List<DelegateWalkPost> findAllBySelectedApplicantMemberIdAndScheduledTimeBetween(Long selectedApplicantMemberId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
