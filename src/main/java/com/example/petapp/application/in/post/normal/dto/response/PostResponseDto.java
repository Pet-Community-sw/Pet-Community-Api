package com.example.petapp.application.in.post.normal.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDto {

    private Long postId;

    private String postImageUrl;

    private Long memberId;

    private String memberName;

    private String memberImageUrl;

    @JsonIgnore
    private LocalDateTime createdAt;

    private Long viewCount;

    private Long likeCount;

    private String title;

    private boolean like;

    private String postBeforeTime;

    public PostResponseDto(Long postId, String postImageUrl, Long memberId, String memberName, String memberImageUrl, LocalDateTime createdAt, Long viewCount, Long likeCount, String title, boolean like) {
        this.postId = postId;
        this.postImageUrl = postImageUrl;
        this.memberId = memberId;
        this.memberName = memberName;
        this.memberImageUrl = memberImageUrl;
        this.createdAt = createdAt;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.title = title;
        this.like = like;
    }

}
