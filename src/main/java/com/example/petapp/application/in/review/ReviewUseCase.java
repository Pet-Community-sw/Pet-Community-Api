package com.example.petapp.application.in.review;

import com.example.petapp.application.in.review.dto.request.CreateReviewDto;
import com.example.petapp.application.in.review.dto.request.UpdateReviewDto;
import com.example.petapp.application.in.review.dto.response.CreateReviewResponseDto;
import com.example.petapp.application.in.review.dto.response.GetReviewListResponseDto;
import com.example.petapp.application.in.review.dto.response.GetReviewResponseDto;
import com.example.petapp.domain.review.model.Review;

public interface ReviewUseCase {
    CreateReviewResponseDto createReview(CreateReviewDto createReviewDto, Long id);

    GetReviewResponseDto getReview(Long reviewId, Long id);

    void updateReview(Long reviewId, UpdateReviewDto updateReviewDto, Long id);

    void deleteReview(Long reviewId, Long id);

    GetReviewListResponseDto getReviewListByMember(Long memberId, Long id);

    GetReviewListResponseDto getReviewListByProfile(Long profileId, Long id);

    Review findOrThrow(Long id);
}
