package com.example.petapp.infrastructure.database.adapter;

import com.example.petapp.application.in.post.normal.dto.response.PostResponseDto;
import com.example.petapp.domain.post.PostRepository;
import com.example.petapp.domain.post.model.Post;
import com.example.petapp.infrastructure.database.jpa.post.JpaPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryAdapter<T extends Post> implements PostRepository<T> {

    private final JpaPostRepository<T> repository;

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<T> find(Long id) {
        return repository.findById(id);
    }

    @Override
    public Page<PostResponseDto> findList(Long id, Pageable pageable) {
        return repository.findList(id, pageable);
    }

    @Override
    public Page<PostResponseDto> findListByMember(Long targetId, Long id, Pageable pageable) {
        return repository.findAllByMemberId(targetId, id, pageable);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void incrementViewCount(Long id) {
        repository.incrementViewCount(id);
    }

    @Override
    public void incrementLikeCount(Long id) {
        repository.incrementLikeCount(id);
    }

    @Override
    public void decrementLikeCount(Long id) {
        repository.decrementLikeCount(id);
    }
}
