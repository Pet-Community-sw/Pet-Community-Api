package com.example.petapp.application.usecase.post.delegate.model.dto.request;


import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {

    @NotNull(message = "게시물 id는 필수입니다.")
    private Long postId;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

}
