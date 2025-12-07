package com.example.petapp.domain.like;

import com.example.petapp.application.in.like.dto.request.LikeCountDto;
import com.example.petapp.domain.like.model.Like;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.model.Post;

import java.util.List;

public interface LikeRepository {

    void save(Like like);

    void delete(Like like);

    List<LikeCountDto> countByPosts(List<Long> postIds);

    Long countByPost(Post post);

    Boolean exist(Post post, Member member);
}
