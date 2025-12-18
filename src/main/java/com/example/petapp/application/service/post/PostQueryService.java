package com.example.petapp.application.service.post;

import com.example.petapp.application.in.post.PostQueryUseCase;
import com.example.petapp.domain.post.PostRepository;
import com.example.petapp.domain.post.model.Post;
import com.example.petapp.interfaces.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class PostQueryService<T extends Post> implements PostQueryUseCase<T> {

    private final PostRepository<T> repository;

    @Override
    public T findOrThrow(Long id) {
        return repository.find(id).orElseThrow(() -> new NotFoundException("해당 게시글은 없습니다."));
    }

    @Override
    public Page<T> findList(Pageable pageable) {
        return repository.findList(pageable);
    }

    @Override
    public Page<T> findList(Long memberId, Pageable pageable) {
        return repository.findList(memberId, pageable);
    }


}
