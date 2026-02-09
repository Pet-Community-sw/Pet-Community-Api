package com.example.petapp.infrastructure.database.adapter;

import com.example.petapp.application.in.like.dto.request.LikeCountDto;
import com.example.petapp.domain.like.LikeRepository;
import com.example.petapp.domain.like.model.Like;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.model.Post;
import com.example.petapp.infrastructure.database.jpa.like.JpaLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryAdapter implements LikeRepository {

    private final JpaLikeRepository repository;

    @Override
    public void save(Like like) {
        repository.save(like);
    }

    @Override
    public void delete(Like like) {
        repository.delete(like);
    }

    @Override
    public List<LikeCountDto> countByPosts(List<Long> postIds) {
        return repository.countByPostIds(postIds);
    }

    @Override
    public Long countByPost(Post post) {
        return repository.countByPost(post);
    }

    @Override
    public Boolean exist(Post post, Member member) {
        return repository.existsByPostAndMember(post, member);
    }
}
