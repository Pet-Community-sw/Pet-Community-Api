package com.example.petapp.domain.post;

import com.example.petapp.domain.post.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostRepository<T extends Post> {
    T save(T entity);

    Optional<T> find(Long id);

    Page<T> findList(Pageable pageable);

    Page<T> findList(Long memberId, Pageable pageable);

    void delete(Long id);

    void incrementViewCount(Long id);
}
