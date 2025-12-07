package com.example.petapp.domain.query;

import com.example.petapp.common.exception.NotFoundException;
import com.example.petapp.domain.walkingtogethermatch.WalkingTogetherMatchRepository;
import com.example.petapp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import com.example.petapp.domain.walkrecord.WalkRecordRepository;
import com.example.petapp.domain.walkrecord.model.entity.WalkRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueryServiceImpl implements QueryService {

    private final WalkingTogetherMatchRepository walkingTogetherMatchRepository;
    private final WalkRecordRepository walkRecordRepository;


    @Override
    public WalkingTogetherMatch findByWalkingTogetherPost(Long walkingTogetherPostId) {
        return walkingTogetherMatchRepository.findById(walkingTogetherPostId).orElseThrow(() -> new NotFoundException("해당 함께 산책해요 게시글은 없습니다."));
    }

    @Override
    public WalkRecord findByWalkRecord(Long walkRecordId) {
        return walkRecordRepository.findById(walkRecordId).orElseThrow(() -> new NotFoundException("해당 산책기록은 없습니다."));
    }
}
