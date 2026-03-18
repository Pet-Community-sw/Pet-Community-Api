package com.example.petapp.application.service.match;

import com.example.petapp.application.in.match.WalkingTogetherPostQueryUseCase;
import com.example.petapp.domain.walkingtogetherPost.WalkingTogetherPostRepository;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;
import com.example.petapp.interfaces.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalkingTogetherPostQueryService implements WalkingTogetherPostQueryUseCase {

    private final WalkingTogetherPostRepository repository;

    @Override
    public WalkingTogetherPost findOrThrow(Long id) {
        return repository.find(id).orElseThrow(() -> new NotFoundException("해당 함께 산책해요 게시글은 없습니다."));
    }
}
