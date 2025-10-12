package com.example.petapp.domain.like.model.dto.response;

import com.example.petapp.domain.like.model.dto.request.LikeListDto;
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
