package com.example.petapp.domain.review;

import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.model.entity.Profile;
import com.example.petapp.domain.review.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.petapp.domain.review.model.entity.Review.ReviewType;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByMemberAndReviewType(Member member, ReviewType reviewType);

    List<Review> findAllByProfileAndReviewType(Profile profile, ReviewType reviewType);
}
