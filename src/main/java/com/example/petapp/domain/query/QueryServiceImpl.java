package com.example.petapp.domain.query;

import com.example.petapp.common.exception.NotFoundException;
import com.example.petapp.domain.walkingtogethermatch.WalkingTogetherMatchRepository;
import com.example.petapp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueryServiceImpl implements QueryService {

    private final WalkingTogetherMatchRepository walkingTogetherMatchRepository;


    @Override
    public WalkingTogetherMatch findByWalkingTogetherPost(Long walkingTogetherPostId) {
        return walkingTogetherMatchRepository.findById(walkingTogetherPostId).orElseThrow(() -> new NotFoundException("해당 함께 산책해요 게시글은 없습니다."));
    }

}
