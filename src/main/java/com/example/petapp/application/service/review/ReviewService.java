package com.example.petapp.application.service.review;

import com.example.petapp.application.in.member.MemberUseCase;
import com.example.petapp.application.in.profile.ProfileUseCase;
import com.example.petapp.application.in.review.ReviewUseCase;
import com.example.petapp.application.in.review.dto.request.CreateReviewDto;
import com.example.petapp.application.in.review.dto.request.UpdateReviewDto;
import com.example.petapp.application.in.review.dto.response.CreateReviewResponseDto;
import com.example.petapp.application.in.review.dto.response.GetReviewListResponseDto;
import com.example.petapp.application.in.review.dto.response.GetReviewResponseDto;
import com.example.petapp.application.in.review.mapper.ReviewMapper;
import com.example.petapp.application.in.walkrecord.WalkRecordUseCase;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.review.ReviewRepository;
import com.example.petapp.domain.review.model.Review;
import com.example.petapp.domain.walkrecord.model.WalkRecord;
import com.example.petapp.interfaces.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.petapp.domain.review.model.Review.ReviewType;

@Service
@RequiredArgsConstructor
public class ReviewService implements ReviewUseCase {

    private final ProfileUseCase profileUseCase;
    private final ReviewRepository reviewRepository;
    private final WalkRecordUseCase walkRecordUseCase;
    private final MemberUseCase memberUseCase;

    @Transactional
    @Override
    public CreateReviewResponseDto createReview(CreateReviewDto createReviewDto, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        WalkRecord walkRecord = walkRecordUseCase.findOrThrow(createReviewDto.getWalkRecordId());

        walkRecord.validatedForCreate(member);
        Review savedReview = reviewRepository.save(ReviewMapper.toEntity(walkRecord, createReviewDto));
        return new CreateReviewResponseDto(savedReview.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public GetReviewListResponseDto getReviewListByMember(Long memberId, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        Member ownerMember = memberUseCase.findOrThrow(id);
        List<Review> reviewList = reviewRepository.findAllByMemberAndReviewType(member, ReviewType.PROFILE_TO_MEMBER);
        return ReviewMapper.toGetReviewListResponseDto(reviewList, ownerMember.getId(), ownerMember.getName(), ownerMember.getMemberImageUrl(), ReviewMapper.toGetReviewList(reviewList, member));
    }

    @Transactional(readOnly = true)
    @Override
    public GetReviewListResponseDto getReviewListByProfile(Long profileId, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        Profile profile = profileUseCase.findOrThrow(profileId);
        List<Review> reviewList = reviewRepository.findAllByProfileAndReviewType(profile, ReviewType.MEMBER_TO_PROFILE);
        return ReviewMapper.toGetReviewListResponseDto(reviewList, profile.getId(), profile.getPetName(), profile.getPetImageUrl(), ReviewMapper.toGetReviewList(reviewList, member));
    }


    @Transactional(readOnly = true)
    @Override
    public GetReviewResponseDto getReview(Long reviewId, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        Review review = findOrThrow(reviewId);
        return ReviewMapper.toGetReviewResponseDto(review, member);
    }

    @Transactional
    @Override
    public void updateReview(Long reviewId, UpdateReviewDto updateReviewDto, Long id) {
        Review review = findReviewWithAuth(reviewId, id);
        review.update(updateReviewDto);
    }

    @Transactional
    @Override
    public void deleteReview(Long reviewId, Long id) {
        Review review = findReviewWithAuth(reviewId, id);

        reviewRepository.delete(review.getId());
    }

    private Review findReviewWithAuth(Long reviewId, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        Review review = findOrThrow(reviewId);

        review.validated(member);

        return review;
    }

    @Transactional(readOnly = true)
    @Override
    public Review findOrThrow(Long id) {
        return reviewRepository.find(id).orElseThrow(() -> new NotFoundException("해당 리뷰는 없습니다."));
    }

}
