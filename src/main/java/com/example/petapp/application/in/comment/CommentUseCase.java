package com.example.petapp.application.in.comment;

import com.example.petapp.application.in.comment.dto.request.CommentDto;
import com.example.petapp.application.in.comment.dto.request.UpdateCommentDto;
import com.example.petapp.application.in.comment.dto.response.CreateCommentResponseDto;
import com.example.petapp.application.in.comment.dto.response.GetCommentsResponseDto;

import java.util.List;

public interface CommentUseCase {
    CreateCommentResponseDto createComment(CommentDto commentDto, String email);

    void deleteComment(Long commentId, String email);

    void updateComment(Long commentId, UpdateCommentDto updateCommentDto, String email);

    List<GetCommentsResponseDto> getComments(Long postId, String email);
}
