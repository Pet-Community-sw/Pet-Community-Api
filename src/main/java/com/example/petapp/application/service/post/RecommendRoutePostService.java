package com.example.petapp.application.service.post;

import com.example.petapp.application.in.like.LikeQueryUseCase;
import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.application.in.post.PostQueryUseCase;
import com.example.petapp.application.in.post.recommend.RecommendRoutePostUseCase;
import com.example.petapp.application.in.post.recommend.dto.request.CreateRecommendRoutePostDto;
import com.example.petapp.application.in.post.recommend.dto.request.UpdateRecommendRoutePostDto;
import com.example.petapp.application.in.post.recommend.dto.response.CreateRecommendRoutePostResponseDto;
import com.example.petapp.application.in.post.recommend.dto.response.GetRecommendPostResponseDto;
import com.example.petapp.application.in.post.recommend.dto.response.GetRecommendRoutePostsResponseDto;
import com.example.petapp.application.in.post.recommend.mapper.RecommendRoutePostMapper;
import com.example.petapp.application.out.cache.LikeCachePort;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.PostRepository;
import com.example.petapp.domain.post.RecommendRoutePostRepository;
import com.example.petapp.domain.post.model.RecommendRoutePost;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecommendRoutePostService implements RecommendRoutePostUseCase {

    private final RecommendRoutePostRepository recommendRoutePostRepository;
    private final LikeQueryUseCase likeQueryUseCase;
    private final MemberQueryUseCase memberQueryUseCase;
    private final LikeCachePort port;
    private final PostQueryUseCase<RecommendRoutePost> postQueryUseCase;
    private final PostRepository<RecommendRoutePost> postRepository;

    @Transactional
    @Override
    public CreateRecommendRoutePostResponseDto createRecommendRoutePost(CreateRecommendRoutePostDto createRecommendRoutePostDto, Long id) {
        Member member = memberQueryUseCase.findOrThrow(id);
        RecommendRoutePost recommendRoutePost = RecommendRoutePostMapper.toEntity(createRecommendRoutePostDto, member);
        RecommendRoutePost savedRecommendRoutePost = postRepository.save(recommendRoutePost);
        return new CreateRecommendRoutePostResponseDto(savedRecommendRoutePost.getId());
    }

    @Transactional(readOnly = true)//페이징 처리를 해야됨. 40개 정도 내보내면 프론트가 페이지 처리할 수 있으려나?
    @Override
    public List<GetRecommendRoutePostsResponseDto> getRecommendRoutePosts(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, int page, Long id) {
        Member member = memberQueryUseCase.findOrThrow(id);
        Pageable pageable = PageRequest.of(page - 1, 10);
        Set<Long> memberIds = port.getList(member.getId());
        List<RecommendRoutePost> recommendRoutePosts = recommendRoutePostRepository
                .findList(minLongitude - 0.01, minLatitude - 0.01, maxLongitude + 0.01, maxLatitude + 0.01, pageable)
                .getContent();
        return RecommendRoutePostMapper.toRecommendRoutePostsList(recommendRoutePosts, likeQueryUseCase.getCountMap(recommendRoutePosts), memberIds, member);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetRecommendRoutePostsResponseDto> getRecommendRoutePosts(Double longitude, Double latitude, int page, Long id) {
        Member member = memberQueryUseCase.findOrThrow(id);
        Pageable pageable = PageRequest.of(page - 1, 10);
        Set<Long> memberIds = port.getList(member.getId());
        List<RecommendRoutePost> recommendRoutePosts = recommendRoutePostRepository.findList(longitude, latitude, pageable).getContent();
        return RecommendRoutePostMapper.toRecommendRoutePostsList(recommendRoutePosts, likeQueryUseCase.getCountMap(recommendRoutePosts), memberIds, member);
    }

    @Transactional()
    @Override
    public GetRecommendPostResponseDto getRecommendRoutePost(Long recommendRoutePostId, Long id) {
        Member member = memberQueryUseCase.findOrThrow(id);
        RecommendRoutePost recommendRoutePost = postQueryUseCase.findOrThrow(recommendRoutePostId);
        postRepository.incrementViewCount(recommendRoutePostId);
        return RecommendRoutePostMapper.toGetRecommendPostResponseDto(member, recommendRoutePost, likeQueryUseCase.countByPost(recommendRoutePost), likeQueryUseCase.exist(recommendRoutePost, member));
    }

    @Transactional
    @Override
    public void updateRecommendRoutePost(Long recommendRoutePostId, UpdateRecommendRoutePostDto updateRecommendRoutePostDto, Long id) {
        Member member = memberQueryUseCase.findOrThrow(id);
        RecommendRoutePost recommendRoutePost = postQueryUseCase.findOrThrow(recommendRoutePostId);
        recommendRoutePost.validateMember(member);
        recommendRoutePost.updateContent(updateRecommendRoutePostDto.getTitle(), updateRecommendRoutePostDto.getContent());
    }

    @Transactional
    @Override
    public void deleteRecommendRoutePost(Long recommendRoutePostId, Long id) {
        Member member = memberQueryUseCase.findOrThrow(id);
        RecommendRoutePost recommendRoutePost = postQueryUseCase.findOrThrow(recommendRoutePostId);
        recommendRoutePost.validateMember(member);
        postRepository.delete(recommendRoutePostId);
    }
}
