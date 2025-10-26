package com.example.petapp.domain.post.normal;

import com.example.petapp.domain.post.common.PostRepository;
import com.example.petapp.domain.post.normal.model.entity.NormalPost;
import org.springframework.stereotype.Repository;

@Repository
public interface NormalPostRepository extends PostRepository<NormalPost> {

}
