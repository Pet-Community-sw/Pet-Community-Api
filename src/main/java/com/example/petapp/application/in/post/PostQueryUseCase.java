package com.example.petapp.application.in.post;

import com.example.petapp.domain.post.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostQueryUseCase<T extends Post> {

    T findOrThrow(Long id);

    Page<T> findList(Pageable pageable);

    Page<T> findList(Long memberId, Pageable pageable);
}
