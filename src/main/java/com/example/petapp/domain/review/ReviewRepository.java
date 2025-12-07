package com.example.petapp.domain.review;

import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.review.model.Review;

import java.util.List;
import java.util.Optional;

import static com.example.petapp.domain.review.model.Review.ReviewType;

public interface ReviewRepository {
    List<Review> findAllByMemberAndReviewType(Member member, ReviewType reviewType);

    List<Review> findAllByProfileAndReviewType(Profile profile, ReviewType reviewType);

    Review save(Review review);

    void delete(Long id);

    Optional<Review> find(Long id);
}
