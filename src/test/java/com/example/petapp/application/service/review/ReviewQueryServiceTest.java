package com.example.petapp.application.service.review;

import com.example.petapp.domain.review.ReviewRepository;
import com.example.petapp.domain.review.model.Review;
import com.example.petapp.interfaces.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewQueryServiceTest {

    @Mock
    private ReviewRepository repository;

    @InjectMocks
    private ReviewQueryService reviewQueryService;

    @Test
    void 리뷰가_존재하면_조회에_성공한다() {
        Review review = org.mockito.Mockito.mock(Review.class);
        when(repository.find(1L)).thenReturn(Optional.of(review));

        Review result = reviewQueryService.findOrThrow(1L);

        assertThat(result).isSameAs(review);
        verify(repository).find(1L);
    }

    @Test
    void 리뷰가_없으면_예외가_발생한다() {
        when(repository.find(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewQueryService.findOrThrow(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 리뷰는 없습니다.");
    }
}
