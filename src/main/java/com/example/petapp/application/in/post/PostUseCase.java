package com.example.petapp.application.in.post;

import com.example.petapp.application.in.post.normal.dto.response.PostResponseDto;
import com.example.petapp.domain.post.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostUseCase<T extends Post> {

    T findOrThrow(Long id);

    Page<PostResponseDto> findListByMember(Long targetId, Long id, Pageable pageable);

    Page<PostResponseDto> findList(Long id, Pageable pageable);

}
