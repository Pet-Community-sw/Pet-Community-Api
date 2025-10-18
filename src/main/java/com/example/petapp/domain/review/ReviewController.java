package com.example.petapp.domain.review;

import com.example.petapp.common.base.dto.MessageResponse;
import com.example.petapp.common.base.util.AuthUtil;
import com.example.petapp.domain.review.model.dto.request.CreateReviewDto;
import com.example.petapp.domain.review.model.dto.request.UpdateReviewDto;
import com.example.petapp.domain.review.model.dto.response.CreateReviewResponseDto;
import com.example.petapp.domain.review.model.dto.response.GetReviewListResponseDto;
import com.example.petapp.domain.review.model.dto.response.GetReviewResponseDto;
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

    private final ReviewService reviewService;

    @Operation(
            summary = "리뷰 생성"
    )
    @PostMapping
    public CreateReviewResponseDto createReview(@RequestBody @Valid CreateReviewDto createReviewDto, Authentication authentication) {
        return reviewService.createReview(createReviewDto, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "리뷰 목록 조회 by-member"
    )
    @GetMapping("/{memberId}/list/member")
    public GetReviewListResponseDto getReviewListByMember(@PathVariable Long memberId, Authentication authentication) {
        return reviewService.getReviewListByMember(memberId, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "리뷰 목록 조회 by-profile"
    )
    @GetMapping("/{profileId}/list/profile")
    public GetReviewListResponseDto getReviewListByProfile(@PathVariable Long profileId, Authentication authentication) {
        return reviewService.getReviewListByProfile(profileId, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "리뷰 상세 조회"
    )
    @GetMapping("/{reviewId}")
    public GetReviewResponseDto getReview(@PathVariable Long reviewId, Authentication authentication) {
        return reviewService.getReview(reviewId, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "리뷰 수정"
    )
    @PutMapping("/{reviewId}")
    public ResponseEntity<MessageResponse> updateReview(@PathVariable Long reviewId, @RequestBody @Valid UpdateReviewDto updateReviewDto, Authentication authentication) {
        reviewService.updateReview(reviewId, updateReviewDto, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("수정 되었습니다."));
    }

    @Operation(
            summary = "리뷰 삭제"
    )
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<MessageResponse> deleteReview(@PathVariable Long reviewId, Authentication authentication) {
        reviewService.deleteReview(reviewId, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("삭제 되었습니다."));
    }

}
