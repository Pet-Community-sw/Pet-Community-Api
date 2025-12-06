package com.example.petapp.application.in.post.recommend.dto.response;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetRecommendPostResponseDto {
    private Long recommendRoutePostId;

    private String title;

    private String content;

    private Long memberId;

    private String memberName;

    private String memberImageUrl;

    private String createdAt;

    private Long likeCount;

    private Long viewCount;

    private boolean isOwner;

    private boolean isLike;
}
