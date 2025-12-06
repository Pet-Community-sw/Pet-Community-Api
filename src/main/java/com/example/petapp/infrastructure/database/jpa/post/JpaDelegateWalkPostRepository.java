package com.example.petapp.infrastructure.database.jpa.post;

import com.example.petapp.domain.post.model.DelegateWalkPost;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaDelegateWalkPostRepository extends JpaRepository<DelegateWalkPost, Long> {
    @Query("select d from DelegateWalkPost d " +
            "where st_distance_sphere(point(d.location.locationLongitude, d.location.locationLatitude), point(:longitude, :latitude)) <= 1000" +
            "order by d.createdAt desc"
    )
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
