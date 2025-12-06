package com.example.petapp.application.in.post.delegate.model.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetCommentsResponseDto {

    private Long commentId;

    private String content;

    private Long memberId;

    private String memberName;

    private String memberImageUrl;

    private String createdAt;

    private boolean isOwner;

}
