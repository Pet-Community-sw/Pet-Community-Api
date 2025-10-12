package com.example.petapp.domain.like.model.dto.request;

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
