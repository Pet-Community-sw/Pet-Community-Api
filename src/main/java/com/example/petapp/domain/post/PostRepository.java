package com.example.petapp.domain.post;

import com.example.petapp.application.in.post.normal.dto.response.PostResponseDto;
import com.example.petapp.domain.post.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostRepository<T extends Post> {
    T save(T entity);

    Optional<T> find(Long id);

    Page<PostResponseDto> findList(Long id, Pageable pageable);

    Page<PostResponseDto> findListByMember(Long targetId, Long id, Pageable pageable);

    void delete(Long id);

    void incrementViewCount(Long id);

    void incrementLikeCount(Long id);

    void decrementLikeCount(Long id);
}
