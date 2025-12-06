package com.example.petapp.domain.like;

import com.example.petapp.domain.like.model.dto.response.LikeResponseDto;
import com.example.petapp.domain.post.model.Post;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface LikeService {

    boolean createAndDeleteLike(Long postId, String email);

    LikeResponseDto getLikes(Long postId);

    <T extends Post> Map<Long, Long> getLikeCountMap(List<T> posts);
}
