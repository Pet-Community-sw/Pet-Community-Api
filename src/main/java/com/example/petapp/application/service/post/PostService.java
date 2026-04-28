package com.example.petapp.application.service.post;

import com.example.petapp.application.in.post.PostUseCase;
import com.example.petapp.application.in.post.normal.dto.response.PostResponseDto;
import com.example.petapp.domain.post.PostRepository;
import com.example.petapp.domain.post.model.Post;
import com.example.petapp.interfaces.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService<T extends Post> implements PostUseCase<T> {

    private final PostRepository<T> repository;

    @Transactional(readOnly = true)
    @Override
    public T findOrThrow(Long id) {
        return repository.find(id).orElseThrow(() -> new NotFoundException("해당 게시글은 없습니다."));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<PostResponseDto> findListByMember(Long targetId, Long id, Pageable pageable) {
        return repository.findListByMember(targetId, id, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<PostResponseDto> findList(Long id, Pageable pageable) {
        return repository.findList(id, pageable);
    }
}
