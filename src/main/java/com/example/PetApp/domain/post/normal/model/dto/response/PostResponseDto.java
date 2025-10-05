package com.example.PetApp.domain.post.normal.model.dto.response;

import com.example.PetApp.domain.comment.model.dto.response.GetCommentsResponseDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPostResponseDto {

    private Long postId;

    private String postImageUrl;

    private Long memberId;

    private String content;

    private String memberName;

    private String memberImageUrl;

    private String createdAt;

    private Long viewCount;

    private Long likeCount;

    private String title;

    private boolean like;

    private boolean isOwner;

    List<GetCommentsResponseDto> comments;

}
