package com.example.PetApp.domain.review;

import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.domain.review.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.PetApp.domain.review.model.entity.Review.*;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByMemberAndReviewType(Member member, ReviewType reviewType);

    List<Review> findAllByProfileAndReviewType(Profile profile, ReviewType reviewType);
}
