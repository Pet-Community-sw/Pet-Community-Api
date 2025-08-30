package com.example.PetApp.domain.review;

import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.domain.review.model.dto.request.CreateReviewDto;
import com.example.PetApp.domain.review.model.dto.request.UpdateReviewDto;
import com.example.PetApp.domain.review.model.dto.response.CreateReviewResponseDto;
import com.example.PetApp.domain.review.model.dto.response.GetReviewListResponseDto;
import com.example.PetApp.domain.review.model.dto.response.GetReviewResponseDto;
import com.example.PetApp.domain.review.model.entity.Review;
import com.example.PetApp.domain.walkrecord.model.entity.WalkRecord;
import com.example.PetApp.infrastructure.database.shared.embedded.Content;
import com.example.PetApp.common.exception.ConflictException;
import com.example.PetApp.common.exception.ForbiddenException;
import com.example.PetApp.domain.review.mapper.ReviewMapper;
import com.example.PetApp.domain.query.QueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.PetApp.domain.review.model.entity.Review.*;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService{

    private final ReviewRepository reviewRepository;
    private final QueryService queryService;

    @Transactional
    @Override
    public CreateReviewResponseDto createReview(CreateReviewDto createReviewDto, String email) {
        Member member = queryService.findByMember(email);
        WalkRecord walkRecord = queryService.findByWalkRecord(createReviewDto.getWalkRecordId());
        if (walkRecord.getWalkStatus() != WalkRecord.WalkStatus.FINISH) {
            throw new ConflictException("산책을 다해야 후기를 작성할 수 있습니다.");
        } else if (!(walkRecord.getMember().equals(member))) {
            throw new ForbiddenException("권한이 없습니다.");
        }
        Review savedReview = reviewRepository.save(ReviewMapper.toEntity(walkRecord, createReviewDto));
        return new CreateReviewResponseDto(savedReview.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public GetReviewListResponseDto getReviewListByMember(Long memberId, String email) {
        Member member = queryService.findByMember(email);
        Member ownerMember = queryService.findByMember(memberId);
        List<Review> reviewList = reviewRepository.findAllByMemberAndReviewType(member, ReviewType.PROFILE_TO_MEMBER);
        return ReviewMapper.toGetReviewListResponseDto(reviewList, ownerMember.getId(), ownerMember.getName(), ownerMember.getMemberImageUrl(), ReviewMapper.toGetReviewList(reviewList, member));
    }

    @Transactional(readOnly = true)
    @Override
    public GetReviewListResponseDto getReviewListByProfile(Long profileId, String email) {
        Member member = queryService.findByMember(email);
        Profile profile = queryService.findByProfile(profileId);
        List<Review> reviewList = reviewRepository.findAllByProfileAndReviewType(profile, ReviewType.MEMBER_TO_PROFILE);
        return ReviewMapper.toGetReviewListResponseDto(reviewList, profile.getId(), profile.getPetName(), profile.getPetImageUrl(), ReviewMapper.toGetReviewList(reviewList, member));

    }


    @Transactional(readOnly = true)
    @Override
    public GetReviewResponseDto getReview(Long reviewId, String email) {
        Member member = queryService.findByMember(email);
        Review review = queryService.findByReview(reviewId);
        return ReviewMapper.toGetReviewResponseDto(review, member);
    }

    @Transactional
    @Override
    public void updateReview(Long reviewId, UpdateReviewDto updateReviewDto, String email) {
        Review review = findReviewWithAuth(reviewId, email);

        review.setContent(new Content(updateReviewDto.getTitle(), updateReviewDto.getContent()));
        review.setRating(updateReviewDto.getRating());
    }

    @Transactional
    @Override
    public void deleteReview(Long reviewId, String email) {
        Review review = findReviewWithAuth(reviewId, email);

        reviewRepository.deleteById(review.getId());
    }

    private Review findReviewWithAuth(Long reviewId, String email) {
        Member member = queryService.findByMember(email);
        Review review = queryService.findByReview(reviewId);

        if (review.getReviewType() == ReviewType.MEMBER_TO_PROFILE) {
            if (!review.getMember().equals(member)) {
                throw new ForbiddenException("권한이 없습니다.");
            }
        } else if (review.getReviewType() == ReviewType.PROFILE_TO_MEMBER) {
            if (!review.getProfile().getMember().equals(member)) {
                throw new ForbiddenException("권한이 없습니다.");
            }
        }

        return review;
    }

}
