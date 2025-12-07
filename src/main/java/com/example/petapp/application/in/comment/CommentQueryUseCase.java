package com.example.petapp.application.in.comment;

import com.example.petapp.domain.comment.model.Comment;

public interface CommentQueryUseCase {
    Comment findOrThrow(Long id);
}
