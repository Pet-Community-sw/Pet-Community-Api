package com.example.petapp.domain.comment;

import com.example.petapp.domain.comment.model.dto.request.CommentDto;
import com.example.petapp.domain.comment.model.dto.response.CreateCommentResponseDto;
import com.example.petapp.domain.comment.model.dto.response.GetCommentsResponseDto;
import com.example.petapp.domain.comment.model.dto.request.UpdateCommentDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {
    CreateCommentResponseDto createComment(CommentDto commentDto, String email);

    void deleteComment(Long commentId, String email);

    void updateComment(Long commentId, UpdateCommentDto updateCommentDto, String  email);

    List<GetCommentsResponseDto> getComments(Long postId, String email);
}
