package com.example.petapp.application.in.post;

import com.example.petapp.domain.post.Post;

public interface PostQueryUseCase<T extends Post> {

    T findOrThrow(Long id);
}
