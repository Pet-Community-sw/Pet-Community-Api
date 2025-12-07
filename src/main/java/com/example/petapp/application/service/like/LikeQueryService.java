package com.example.petapp.application.service.like;

import com.example.petapp.application.in.like.LikeQueryUseCase;
import com.example.petapp.application.in.like.dto.request.LikeCountDto;
import com.example.petapp.application.in.like.dto.response.LikeResponseDto;
import com.example.petapp.application.in.like.mapper.LikeMapper;
import com.example.petapp.application.in.post.PostQueryUseCase;
import com.example.petapp.domain.like.LikeRepository;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class LikeQueryService implements LikeQueryUseCase {

    private final LikeRepository repository;
    private final PostQueryUseCase<Post> postQueryUseCase;

    @Override
    public LikeResponseDto get(Long postId) {
        return LikeMapper.toLikeResponseDto(postQueryUseCase.findOrThrow(postId).getLikes());
    }

    @Override
    public <T extends Post> Map<Long, Long> getCountMap(List<T> posts) {
        List<Long> postIds = posts.stream().map(Post::getId).toList();
        List<LikeCountDto> likeCountDtos = repository.countByPosts(postIds);//todo : 추후 redis로 변경해야할듯?
        return likeCountDtos.stream().collect(Collectors.toMap(LikeCountDto::getPostId, LikeCountDto::getLikeCount));
    }

    @Override
    public Long countByPost(Post post) {
        return repository.countByPost(post);
    }

    @Override
    public Boolean exist(Post post, Member member) {
        return repository.exist(post, member);
    }
}
