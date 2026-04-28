package com.example.petapp.application.in.like;

import com.example.petapp.application.in.like.dto.response.LikeResponseDto;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.model.Post;

import java.util.List;
import java.util.Map;

public interface LikeUseCase {

    boolean createAndDelete(Long postId, Long id);

    LikeResponseDto get(Long postId);

    <T extends Post> Map<Long, Long> getCountMap(List<T> posts);

    Long countByPost(Post post);

    Boolean exist(Post post, Member member);
}
