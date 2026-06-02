package com.example.petapp.application.usecase.post.service;

import com.example.petapp.application.common.PagePolicy;
import com.example.petapp.application.common.PostSearchPolicy;
import com.example.petapp.application.out.cache.LikeCachePort;
import com.example.petapp.application.usecase.like.LikeUseCase;
import com.example.petapp.application.usecase.member.MemberUseCase;
import com.example.petapp.application.usecase.post.PostUseCase;
import com.example.petapp.application.usecase.post.recommend.RecommendRoutePostUseCase;
import com.example.petapp.application.usecase.post.recommend.dto.request.CreateRecommendRoutePostDto;
import com.example.petapp.application.usecase.post.recommend.dto.request.UpdateRecommendRoutePostDto;
import com.example.petapp.application.usecase.post.recommend.dto.response.CreateRecommendRoutePostResponseDto;
import com.example.petapp.application.usecase.post.recommend.dto.response.GetRecommendPostResponseDto;
import com.example.petapp.application.usecase.post.recommend.dto.response.GetRecommendRoutePostsResponseDto;
import com.example.petapp.application.usecase.post.recommend.mapper.RecommendRoutePostMapper;
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
    private final LikeUseCase likeUseCase;
    private final MemberUseCase memberUseCase;
    private final LikeCachePort port;
    private final PostUseCase<RecommendRoutePost> postUseCase;
    private final PostRepository<RecommendRoutePost> postRepository;

    @Transactional
    @Override
    public CreateRecommendRoutePostResponseDto createRecommendRoutePost(CreateRecommendRoutePostDto createRecommendRoutePostDto, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        RecommendRoutePost recommendRoutePost = RecommendRoutePostMapper.toEntity(createRecommendRoutePostDto, member);
        RecommendRoutePost savedRecommendRoutePost = postRepository.save(recommendRoutePost);
        return new CreateRecommendRoutePostResponseDto(savedRecommendRoutePost.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetRecommendRoutePostsResponseDto> getRecommendRoutePosts(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, int page, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        Pageable pageable = PageRequest.of(page - 1, PagePolicy.DEFAULT_PAGE_SIZE);
        Set<Long> memberIds = port.findLikedMemberIds(member.getId());
        List<RecommendRoutePost> recommendRoutePosts = recommendRoutePostRepository
                .findList(
                        minLongitude - PostSearchPolicy.BOUNDING_BOX_MARGIN_DEGREES,
                        minLatitude - PostSearchPolicy.BOUNDING_BOX_MARGIN_DEGREES,
                        maxLongitude + PostSearchPolicy.BOUNDING_BOX_MARGIN_DEGREES,
                        maxLatitude + PostSearchPolicy.BOUNDING_BOX_MARGIN_DEGREES,
                        pageable
                )
                .getContent();
        return RecommendRoutePostMapper.toRecommendRoutePostsList(recommendRoutePosts, likeUseCase.getCountMap(recommendRoutePosts), memberIds, member);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetRecommendRoutePostsResponseDto> getRecommendRoutePosts(Double longitude, Double latitude, int page, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        Pageable pageable = PageRequest.of(page - 1, PagePolicy.DEFAULT_PAGE_SIZE);
        Set<Long> memberIds = port.findLikedMemberIds(member.getId());
        List<RecommendRoutePost> recommendRoutePosts = recommendRoutePostRepository.findList(longitude, latitude, pageable).getContent();
        return RecommendRoutePostMapper.toRecommendRoutePostsList(recommendRoutePosts, likeUseCase.getCountMap(recommendRoutePosts), memberIds, member);
    }

    @Override
    public GetRecommendPostResponseDto getRecommendRoutePost(Long recommendRoutePostId, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        RecommendRoutePost recommendRoutePost = postUseCase.findOrThrow(recommendRoutePostId);
        postRepository.incrementViewCount(recommendRoutePostId);
        return RecommendRoutePostMapper.toGetRecommendPostResponseDto(member, recommendRoutePost, likeUseCase.countByPost(recommendRoutePost), likeUseCase.exist(recommendRoutePost, member));
    }

    @Transactional
    @Override
    public void updateRecommendRoutePost(Long recommendRoutePostId, UpdateRecommendRoutePostDto updateRecommendRoutePostDto, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        RecommendRoutePost recommendRoutePost = postUseCase.findOrThrow(recommendRoutePostId);
        recommendRoutePost.validateMember(member);
        recommendRoutePost.updateContent(updateRecommendRoutePostDto.getTitle(), updateRecommendRoutePostDto.getContent());
    }

    @Transactional
    @Override
    public void deleteRecommendRoutePost(Long recommendRoutePostId, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        RecommendRoutePost recommendRoutePost = postUseCase.findOrThrow(recommendRoutePostId);
        recommendRoutePost.validateMember(member);
        postRepository.delete(recommendRoutePostId);
    }
}
