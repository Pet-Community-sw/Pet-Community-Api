package com.example.petapp.domain.review.mapper;

import com.example.petapp.domain.member.model.entity.Member;
import com.example.petapp.domain.review.model.entity.Review;
import com.example.petapp.domain.walkrecord.model.entity.WalkRecord;
import com.example.petapp.common.base.embedded.Content;
import com.example.petapp.domain.review.model.dto.request.CreateReviewDto;
import com.example.petapp.domain.review.model.dto.request.GetReviewList;
import com.example.petapp.domain.review.model.dto.response.GetReviewListResponseDto;
import com.example.petapp.domain.review.model.dto.response.GetReviewResponseDto;

import java.util.List;
import java.util.stream.Collectors;


public class ReviewMapper {
    public static Review toEntity(WalkRecord walkRecord, CreateReviewDto createReviewDto) {
        return Review.builder()
                .member(walkRecord.getMember())
                .profile(walkRecord.getDelegateWalkPost().getProfile())
                .walkRecord(walkRecord)
                .content(new Content(createReviewDto.getTitle(), createReviewDto.getContent()))
                .rating(createReviewDto.getRating())
                .reviewType(createReviewDto.getReviewType())
                .build();
    }

    public static GetReviewResponseDto toGetReviewResponseDto(Review review, Member member) {
        GetReviewResponseDto getReviewResponseDto = GetReviewResponseDto.builder()
                .reviewId(review.getId())
                .title(review.getContent().getTitle())
                .content(review.getContent().getContent())
                .rating(review.getRating())
                .reviewTime(review.getCreatedAt())
                .build();
        if (review.getReviewType() == Review.ReviewType.PROFILE_TO_MEMBER) {
            getReviewResponseDto.setUserId(review.getProfile().getId());
            getReviewResponseDto.setUserName(review.getProfile().getPetName());
            getReviewResponseDto.setUserImageUrl(review.getProfile().getPetImageUrl());
            getReviewResponseDto.setOwner(member.equals(review.getProfile().getMember()));
        } else {
            getReviewResponseDto.setUserId(review.getMember().getId());
            getReviewResponseDto.setUserName(review.getMember().getName());
            getReviewResponseDto.setUserImageUrl(review.getMember().getMemberImageUrl());
            getReviewResponseDto.setOwner(member.equals(review.getMember()));
        }
        return getReviewResponseDto;
    }

    public static GetReviewListResponseDto toGetReviewListResponseDto(List<Review> reviews, Long userId, String userName, String userImageUrl, List<GetReviewList> getReviewLists) {
        return GetReviewListResponseDto.builder()
                .userId(userId)
                .userName(userName)
                .userImageUrl(userImageUrl)
                .averageRating(reviews.stream().mapToInt(Review::getRating).average().orElse(0.0))
                .reviewCount(reviews.size())
                .reviewList(getReviewLists)
                .build();

    }

    public static List<GetReviewList> toGetReviewList(List<Review> reviews, Member member) {
        return reviews.stream()
                .map(review -> GetReviewList.builder()
                        .reviewId(review.getId())
                        .userId(review.getProfile().getId())
                        .userName(review.getProfile().getPetName())
                        .userImageUrl(review.getProfile().getPetImageUrl())
                        .title(review.getContent().getTitle())
                        .rating(review.getRating())
                        .reviewTime(review.getCreatedAt())
                        .isOwner(review.getMember().equals(member))
                        .build()
                ).collect(Collectors.toList());
    }
}
