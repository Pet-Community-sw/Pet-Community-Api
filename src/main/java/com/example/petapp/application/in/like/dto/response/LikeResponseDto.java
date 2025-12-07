package com.example.petapp.application.in.like.dto.response;

import com.example.petapp.application.in.like.dto.request.LikeListDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeResponseDto {
    private List<LikeListDto> likeListDtos;

    private Long likeCount;


}
