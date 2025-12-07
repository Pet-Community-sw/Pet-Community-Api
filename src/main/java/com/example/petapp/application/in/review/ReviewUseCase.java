package com.example.petapp.application.in.review;

import com.example.petapp.application.in.review.dto.request.CreateReviewDto;
import com.example.petapp.application.in.review.dto.request.UpdateReviewDto;
import com.example.petapp.application.in.review.dto.response.CreateReviewResponseDto;
import com.example.petapp.application.in.review.dto.response.GetReviewListResponseDto;
import com.example.petapp.application.in.review.dto.response.GetReviewResponseDto;

public interface ReviewUseCase {
    CreateReviewResponseDto createReview(CreateReviewDto createReviewDto, String email);

    GetReviewResponseDto getReview(Long reviewId, String email);

    void updateReview(Long reviewId, UpdateReviewDto updateReviewDto, String email);

    void deleteReview(Long reviewId, String email);

    GetReviewListResponseDto getReviewListByMember(Long memberId, String email);

    GetReviewListResponseDto getReviewListByProfile(Long profileId, String email);
}
