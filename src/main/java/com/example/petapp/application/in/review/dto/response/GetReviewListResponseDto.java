package com.example.petapp.application.in.review.dto.response;

import com.example.petapp.application.in.review.dto.request.GetReviewList;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetReviewListResponseDto {

    List<GetReviewList> reviewList;
    private Long userId;
    private String userName;
    private String userImageUrl;
    private Double averageRating;
    private int reviewCount;
}
