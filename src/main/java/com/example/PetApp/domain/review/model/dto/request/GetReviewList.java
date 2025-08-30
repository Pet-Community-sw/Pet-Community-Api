package com.example.PetApp.domain.review.model.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetReviewList {

    private Long reviewId;

    private Long userId;

    private String userName;

    private String userImageUrl;

    private String title;

    private Integer rating;

    private LocalDateTime reviewTime;

    private boolean isOwner;

}
