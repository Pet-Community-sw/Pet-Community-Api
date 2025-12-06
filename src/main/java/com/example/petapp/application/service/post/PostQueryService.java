package com.example.petapp.application.service.post;

import com.example.petapp.application.in.post.PostQueryUseCase;
import com.example.petapp.common.exception.NotFoundException;
import com.example.petapp.domain.post.Post;
import com.example.petapp.domain.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostQueryService<T extends Post> implements PostQueryUseCase<T> {

    private final PostRepository<T> repository;

    @Override
    public T findOrThrow(Long id) {
        return repository.find(id).orElseThrow(() -> new NotFoundException("해당 게시글은 없습니다."));
    }
}
