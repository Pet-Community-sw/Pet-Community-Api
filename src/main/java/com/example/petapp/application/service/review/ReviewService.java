package com.example.petapp.application.service.review;

import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.application.in.profile.ProfileQueryUseCase;
import com.example.petapp.application.in.review.ReviewQueryUseCase;
import com.example.petapp.application.in.review.ReviewUseCase;
import com.example.petapp.application.in.review.dto.request.CreateReviewDto;
import com.example.petapp.application.in.review.dto.request.UpdateReviewDto;
import com.example.petapp.application.in.review.dto.response.CreateReviewResponseDto;
import com.example.petapp.application.in.review.dto.response.GetReviewListResponseDto;
import com.example.petapp.application.in.review.dto.response.GetReviewResponseDto;
import com.example.petapp.application.in.review.mapper.ReviewMapper;
import com.example.petapp.application.in.walkrecord.WalkRecordQueryUseCase;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.review.ReviewRepository;
import com.example.petapp.domain.review.model.Review;
import com.example.petapp.domain.walkrecord.model.WalkRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.petapp.domain.review.model.Review.ReviewType;

@Service
@RequiredArgsConstructor
public class ReviewService implements ReviewUseCase {

    private final ProfileQueryUseCase profileQueryUseCase;
    private final ReviewRepository reviewRepository;
    private final WalkRecordQueryUseCase walkRecordQueryUseCase;
    private final ReviewQueryUseCase reviewQueryUseCase;
    private final MemberQueryUseCase memberQueryUseCase;

    @Transactional
    @Override
    public CreateReviewResponseDto createReview(CreateReviewDto createReviewDto, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        WalkRecord walkRecord = walkRecordQueryUseCase.findOrThrow(createReviewDto.getWalkRecordId());

        walkRecord.validatedForCreate(member);
        Review savedReview = reviewRepository.save(ReviewMapper.toEntity(walkRecord, createReviewDto));
        return new CreateReviewResponseDto(savedReview.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public GetReviewListResponseDto getReviewListByMember(Long memberId, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        Member ownerMember = memberQueryUseCase.findOrThrow(email);
        List<Review> reviewList = reviewRepository.findAllByMemberAndReviewType(member, ReviewType.PROFILE_TO_MEMBER);
        return ReviewMapper.toGetReviewListResponseDto(reviewList, ownerMember.getId(), ownerMember.getName(), ownerMember.getMemberImageUrl(), ReviewMapper.toGetReviewList(reviewList, member));
    }

    @Transactional(readOnly = true)
    @Override
    public GetReviewListResponseDto getReviewListByProfile(Long profileId, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        Profile profile = profileQueryUseCase.findOrThrow(profileId);
        List<Review> reviewList = reviewRepository.findAllByProfileAndReviewType(profile, ReviewType.MEMBER_TO_PROFILE);
        return ReviewMapper.toGetReviewListResponseDto(reviewList, profile.getId(), profile.getPetName(), profile.getPetImageUrl(), ReviewMapper.toGetReviewList(reviewList, member));
    }


    @Transactional(readOnly = true)
    @Override
    public GetReviewResponseDto getReview(Long reviewId, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        Review review = reviewQueryUseCase.findOrThrow(reviewId);
        return ReviewMapper.toGetReviewResponseDto(review, member);
    }

    @Transactional
    @Override
    public void updateReview(Long reviewId, UpdateReviewDto updateReviewDto, String email) {
        Review review = findReviewWithAuth(reviewId, email);
        review.update(updateReviewDto);
    }

    @Transactional
    @Override
    public void deleteReview(Long reviewId, String email) {
        Review review = findReviewWithAuth(reviewId, email);

        reviewRepository.delete(review.getId());
    }

    private Review findReviewWithAuth(Long reviewId, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        Review review = reviewQueryUseCase.findOrThrow(reviewId);

        review.validated(member);

        return review;
    }

}
