package com.example.petapp.infrastructure.database.jpa.review;

import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByMemberAndReviewType(Member member, Review.ReviewType reviewType);

    List<Review> findAllByProfileAndReviewType(Profile profile, Review.ReviewType reviewType);
}
