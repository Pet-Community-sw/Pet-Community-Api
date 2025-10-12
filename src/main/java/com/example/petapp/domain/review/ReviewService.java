package com.example.petapp.domain.review;

import com.example.petapp.domain.review.model.dto.request.CreateReviewDto;
import com.example.petapp.domain.review.model.dto.request.UpdateReviewDto;
import com.example.petapp.domain.review.model.dto.response.CreateReviewResponseDto;
import com.example.petapp.domain.review.model.dto.response.GetReviewListResponseDto;
import com.example.petapp.domain.review.model.dto.response.GetReviewResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface ReviewService {
    CreateReviewResponseDto createReview(CreateReviewDto createReviewDto, String email);

    GetReviewResponseDto getReview(Long reviewId, String email);

    void updateReview(Long reviewId, UpdateReviewDto updateReviewDto, String email);

    void deleteReview(Long reviewId, String email);

    GetReviewListResponseDto getReviewListByMember(Long memberId, String email);

    GetReviewListResponseDto getReviewListByProfile(Long profileId, String email);
}
