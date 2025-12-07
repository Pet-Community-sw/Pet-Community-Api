package com.example.petapp.application.in.post.normal.dto.response;


import com.example.petapp.application.in.comment.dto.response.GetCommentsResponseDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPostResponseDto {

    PostResponseDto postResponseDto;
    List<GetCommentsResponseDto> comments;
    private String content;
    private boolean isOwner;

}
