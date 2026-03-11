package com.example.petapp.interfaces.controller;

import com.example.petapp.application.common.AuthUtil;
import com.example.petapp.application.in.review.ReviewUseCase;
import com.example.petapp.application.in.review.dto.request.CreateReviewDto;
import com.example.petapp.application.in.review.dto.request.UpdateReviewDto;
import com.example.petapp.application.in.review.dto.response.CreateReviewResponseDto;
import com.example.petapp.application.in.review.dto.response.GetReviewListResponseDto;
import com.example.petapp.application.in.review.dto.response.GetReviewResponseDto;
import com.example.petapp.interfaces.dto.MessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Review")
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewUseCase reviewUseCase;

    @Operation(
            summary = "리뷰 생성"
    )
    @PostMapping
    public CreateReviewResponseDto createReview(@RequestBody @Valid CreateReviewDto createReviewDto, Authentication authentication) {
        return reviewUseCase.createReview(createReviewDto, AuthUtil.getMemberId(authentication));
    }

    @Operation(
            summary = "리뷰 목록 조회 by-member"
    )
    @GetMapping("/{memberId}/list/member")
    public GetReviewListResponseDto getReviewListByMember(@PathVariable Long memberId, Authentication authentication) {
        return reviewUseCase.getReviewListByMember(memberId, AuthUtil.getMemberId(authentication));
    }

    @Operation(
            summary = "리뷰 목록 조회 by-profile"
    )
    @GetMapping("/{profileId}/list/profile")
    public GetReviewListResponseDto getReviewListByProfile(@PathVariable Long profileId, Authentication authentication) {
        return reviewUseCase.getReviewListByProfile(profileId, AuthUtil.getMemberId(authentication));
    }

    @Operation(
            summary = "리뷰 상세 조회"
    )
    @GetMapping("/{reviewId}")
    public GetReviewResponseDto getReview(@PathVariable Long reviewId, Authentication authentication) {
        return reviewUseCase.getReview(reviewId, AuthUtil.getMemberId(authentication));
    }

    @Operation(
            summary = "리뷰 수정"
    )
    @PutMapping("/{reviewId}")
    public ResponseEntity<MessageResponse> updateReview(@PathVariable Long reviewId, @RequestBody @Valid UpdateReviewDto updateReviewDto, Authentication authentication) {
        reviewUseCase.updateReview(reviewId, updateReviewDto, AuthUtil.getMemberId(authentication));
        return ResponseEntity.ok(new MessageResponse("수정 되었습니다."));
    }

    @Operation(
            summary = "리뷰 삭제"
    )
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<MessageResponse> deleteReview(@PathVariable Long reviewId, Authentication authentication) {
        reviewUseCase.deleteReview(reviewId, AuthUtil.getMemberId(authentication));
        return ResponseEntity.ok(new MessageResponse("삭제 되었습니다."));
    }

}
