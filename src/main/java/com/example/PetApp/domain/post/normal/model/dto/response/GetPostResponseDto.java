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

    private String content;

    private boolean isOwner;

    PostResponseDto postResponseDto;

    List<GetCommentsResponseDto> comments;

}
