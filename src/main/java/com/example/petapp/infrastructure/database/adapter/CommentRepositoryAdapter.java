package com.example.petapp.infrastructure.database.adapter;

import com.example.petapp.domain.comment.CommentRepository;
import com.example.petapp.domain.comment.model.Comment;
import com.example.petapp.infrastructure.database.jpa.comment.JpaCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryAdapter implements CommentRepository {

    private final JpaCommentRepository repository;

    @Override
    public void save(Comment comment) {
        repository.save(comment);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<Comment> find(Long id) {
        return repository.findById(id);
    }
}
