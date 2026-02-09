package com.example.petapp.infrastructure.database.adapter;

import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.review.ReviewRepository;
import com.example.petapp.domain.review.model.Review;
import com.example.petapp.infrastructure.database.jpa.review.JpaReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryAdapter implements ReviewRepository {

    private final JpaReviewRepository repository;

    @Override
    public List<Review> findAllByMemberAndReviewType(Member member, Review.ReviewType reviewType) {
        return repository.findAllByMemberAndReviewType(member, reviewType);
    }

    @Override
    public List<Review> findAllByProfileAndReviewType(Profile profile, Review.ReviewType reviewType) {
        return repository.findAllByProfileAndReviewType(profile, reviewType);
    }

    @Override
    public Review save(Review review) {
        return repository.save(review);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<Review> find(Long id) {
        return repository.findById(id);
    }
}
