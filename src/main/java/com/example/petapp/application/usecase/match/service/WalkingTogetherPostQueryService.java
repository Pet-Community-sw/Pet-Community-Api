package com.example.petapp.application.usecase.match.service;

import com.example.petapp.application.common.exception.ErrorCode;
import com.example.petapp.application.common.exception.PetCommunityException;
import com.example.petapp.application.usecase.match.WalkingTogetherPostQueryUseCase;
import com.example.petapp.domain.walkingtogetherPost.WalkingTogetherPostRepository;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalkingTogetherPostQueryService implements WalkingTogetherPostQueryUseCase {

    private final WalkingTogetherPostRepository repository;

    @Override
    public WalkingTogetherPost findOrThrow(Long id) {
        return repository.find(id).orElseThrow(() -> new PetCommunityException(ErrorCode.NOT_FOUND, "해당 함께 산책해요 게시글은 없습니다."));
    }
}
