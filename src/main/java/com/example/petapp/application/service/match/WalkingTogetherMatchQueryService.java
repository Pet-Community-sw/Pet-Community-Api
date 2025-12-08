package com.example.petapp.application.service.match;

import com.example.petapp.application.in.match.WalkingTogetherMatchQueryUseCase;
import com.example.petapp.common.exception.NotFoundException;
import com.example.petapp.domain.walkingtogethermatch.WalkingTogetherMatchRepository;
import com.example.petapp.domain.walkingtogethermatch.model.WalkingTogetherMatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalkingTogetherMatchQueryService implements WalkingTogetherMatchQueryUseCase {

    private final WalkingTogetherMatchRepository repository;

    @Override
    public WalkingTogetherMatch findOrThrow(Long id) {
        return repository.find(id).orElseThrow(() -> new NotFoundException("해당 함께 산책해요 게시글은 없습니다."));
    }
}
