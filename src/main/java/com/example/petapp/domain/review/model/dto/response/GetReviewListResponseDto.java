package com.example.petapp.domain.review.model.dto.response;

import com.example.petapp.domain.review.model.dto.request.GetReviewList;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetReviewListResponseDto {

    private Long userId;

    private String userName;

    private String userImageUrl;

    private Double averageRating;

    private int reviewCount;

    List<GetReviewList> reviewList;
}
