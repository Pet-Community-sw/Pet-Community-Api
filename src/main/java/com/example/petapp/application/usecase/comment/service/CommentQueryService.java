package com.example.petapp.application.usecase.comment.service;

import com.example.petapp.application.usecase.comment.CommentQueryUseCase;
import com.example.petapp.domain.comment.CommentRepository;
import com.example.petapp.domain.comment.model.Comment;
import com.example.petapp.interfaces.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CommentQueryService implements CommentQueryUseCase {

    private final CommentRepository repository;

    @Override
    public Comment findOrThrow(Long id) {
        return repository.find(id).orElseThrow(() -> new NotFoundException("해당 댓글은 없습니다."));
    }
}
