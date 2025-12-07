package com.example.petapp.infrastructure.database.jpa.comment;

import com.example.petapp.domain.comment.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCommentRepository extends JpaRepository<Comment, Long> {
}
