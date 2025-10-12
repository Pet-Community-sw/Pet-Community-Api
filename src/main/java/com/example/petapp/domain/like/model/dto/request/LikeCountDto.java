package com.example.petapp.domain.like.model.dto.request;

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
