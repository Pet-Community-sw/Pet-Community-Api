package com.example.petapp.application.usecase.like.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeCountDto {
    private Long postId;

    private Long likeCount;
}
