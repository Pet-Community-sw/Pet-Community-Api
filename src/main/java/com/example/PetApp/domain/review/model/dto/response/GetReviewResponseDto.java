package com.example.PetApp.domain.review.model.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetReviewResponseDto {

    private Long reviewId;

    @Setter
    private Long userId;

    @Setter
    private String userName;

    @Setter
    private String userImageUrl;

    private String title;

    private String content;

    private Integer rating;

    private LocalDateTime reviewTime;

    @Setter
    private boolean isOwner;

}
