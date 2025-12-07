package com.example.petapp.domain.comment;

import com.example.petapp.domain.comment.model.Comment;

import java.util.Optional;

public interface CommentRepository {
    void save(Comment comment);

    void delete(Long id);

    Optional<Comment> find(Long id);
}
