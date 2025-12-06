package com.example.petapp.domain.review;

import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.application.in.profile.ProfileQueryUseCase;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.query.QueryService;
import com.example.petapp.domain.review.mapper.ReviewMapper;
import com.example.petapp.domain.review.model.dto.request.CreateReviewDto;
import com.example.petapp.domain.review.model.dto.request.UpdateReviewDto;
import com.example.petapp.domain.review.model.dto.response.CreateReviewResponseDto;
import com.example.petapp.domain.review.model.dto.response.GetReviewListResponseDto;
import com.example.petapp.domain.review.model.dto.response.GetReviewResponseDto;
import com.example.petapp.domain.review.model.entity.Review;
import com.example.petapp.domain.walkrecord.model.entity.WalkRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.petapp.domain.review.model.entity.Review.ReviewType;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ProfileQueryUseCase profileQueryUseCase;
    private final ReviewRepository reviewRepository;
    private final QueryService queryService;
    private final MemberQueryUseCase memberQueryUseCase;

    @Transactional
    @Override
    public CreateReviewResponseDto createReview(CreateReviewDto createReviewDto, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        WalkRecord walkRecord = queryService.findByWalkRecord(createReviewDto.getWalkRecordId());

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
        Review review = queryService.findByReview(reviewId);
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

        reviewRepository.deleteById(review.getId());
    }

    private Review findReviewWithAuth(Long reviewId, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        Review review = queryService.findByReview(reviewId);

        review.validated(member);

        return review;
    }

}
