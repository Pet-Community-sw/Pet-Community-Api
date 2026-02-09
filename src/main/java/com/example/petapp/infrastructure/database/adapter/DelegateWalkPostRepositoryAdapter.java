package com.example.petapp.infrastructure.database.adapter;

import com.example.petapp.domain.post.DelegateWalkPostRepository;
import com.example.petapp.domain.post.model.DelegateWalkPost;
import com.example.petapp.infrastructure.database.jpa.post.JpaDelegateWalkPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DelegateWalkPostRepositoryAdapter implements DelegateWalkPostRepository {

    private final JpaDelegateWalkPostRepository repository;

    @Override
    public Page<DelegateWalkPost> findList(Double longitude, Double latitude, Pageable pageable) {
        return repository.findByDelegateWalkPostByPlace(longitude, latitude, pageable);
    }

    @Override
    public Page<DelegateWalkPost> findList(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, Pageable pageable) {
        return repository.findByDelegateWalkPostByLocation(minLongitude, minLatitude, maxLongitude, maxLatitude, pageable);
    }

    @Override
    public List<DelegateWalkPost> findList(Long selectedApplicantMemberId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return repository.findAllBySelectedApplicantMemberIdAndScheduledTimeBetween(selectedApplicantMemberId, startDateTime, endDateTime);
    }
}
