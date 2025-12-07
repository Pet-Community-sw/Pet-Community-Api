package com.example.petapp.application.in.like.dto.request;

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
