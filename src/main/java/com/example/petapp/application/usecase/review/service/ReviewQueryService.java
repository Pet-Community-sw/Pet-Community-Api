package com.example.petapp.application.usecase.review.service;

import com.example.petapp.application.usecase.review.ReviewQueryUseCase;
import com.example.petapp.domain.review.ReviewRepository;
import com.example.petapp.domain.review.model.Review;
import com.example.petapp.interfaces.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ReviewQueryService implements ReviewQueryUseCase {

    private final ReviewRepository repository;

    @Override
    public Review findOrThrow(Long id) {
        return repository.find(id).orElseThrow(() -> new NotFoundException("해당 리뷰는 없습니다."));
    }
}
