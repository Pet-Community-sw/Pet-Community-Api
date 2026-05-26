package com.example.petapp.application.usecase.like.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeListDto {

    private Long memberId;
    private String memberName;
    private String memberImageUrl;
}
