package com.example.petapp.application.in.comment;

import com.example.petapp.application.in.comment.dto.request.CommentDto;
import com.example.petapp.application.in.comment.dto.request.UpdateCommentDto;
import com.example.petapp.application.in.comment.dto.response.CreateCommentResponseDto;
import com.example.petapp.application.in.comment.dto.response.GetCommentsResponseDto;

import java.util.List;

public interface CommentUseCase {
    CreateCommentResponseDto createComment(CommentDto commentDto, Long id);

    void deleteComment(Long commentId, Long id);

    void updateComment(Long commentId, UpdateCommentDto updateCommentDto, Long id);

    List<GetCommentsResponseDto> getComments(Long postId, Long id);
}
