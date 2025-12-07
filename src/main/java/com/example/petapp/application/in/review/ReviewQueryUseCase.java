package com.example.petapp.application.in.review;

import com.example.petapp.domain.review.model.Review;

public interface ReviewQueryUseCase {
    Review findOrThrow(Long id);
}
