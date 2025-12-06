package com.example.petapp.domain.post;

import java.util.Optional;

public interface PostRepository<T extends Post> {
    Optional<T> find(Long id);

    void incrementViewCount(Long id);
}
