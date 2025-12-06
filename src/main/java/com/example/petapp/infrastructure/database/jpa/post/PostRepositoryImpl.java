package com.example.petapp.infrastructure.database.jpa.post;

import com.example.petapp.domain.post.Post;
import com.example.petapp.domain.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl<T extends Post> implements PostRepository<T> {

    private final JpaPostRepository<T> repository;

    @Override
    public Optional<T> find(Long id) {
        return repository.findById(id);
    }

    @Override
    public void incrementViewCount(Long id) {
        repository.incrementViewCount(id);
    }
}
