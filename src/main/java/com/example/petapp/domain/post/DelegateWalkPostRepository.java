package com.example.petapp.domain.post;

import com.example.petapp.domain.post.model.DelegateWalkPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface DelegateWalkPostRepository {
    Page<DelegateWalkPost> findList(Double longitude, Double latitude, Pageable pageable);

    Page<DelegateWalkPost> findList(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, Pageable pageable);

    List<DelegateWalkPost> findList(Long selectedApplicantMemberId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
