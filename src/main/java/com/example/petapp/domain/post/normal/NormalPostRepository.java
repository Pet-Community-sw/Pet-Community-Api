package com.example.petapp.domain.post.normal;

import com.example.petapp.domain.post.PostRepository;
import com.example.petapp.domain.post.normal.model.NormalPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface NormalPostRepository extends PostRepository<NormalPost> {

    Page<NormalPost> findAllByMemberId(Long memberId, Pageable page);
}
