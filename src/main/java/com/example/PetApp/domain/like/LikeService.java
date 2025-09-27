package com.example.PetApp.domain.like;

import com.example.PetApp.domain.post.common.Post;
import com.example.PetApp.domain.like.model.dto.response.LikeResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface LikeService {

    boolean createAndDeleteLike(Long postId, String email);

    LikeResponseDto getLikes(Long postId);

    <T extends Post> Map<Long, Long> getLikeCountMap(List<T> posts);
}
