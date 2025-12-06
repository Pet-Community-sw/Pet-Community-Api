package com.example.petapp.domain.post.recommend;

import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.application.in.post.PostQueryUseCase;
import com.example.petapp.domain.like.LikeRepository;
import com.example.petapp.domain.like.LikeService;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.PostRepository;
import com.example.petapp.domain.post.recommend.mapper.RecommendRoutePostMapper;
import com.example.petapp.domain.post.recommend.model.dto.request.CreateRecommendRoutePostDto;
import com.example.petapp.domain.post.recommend.model.dto.request.UpdateRecommendRoutePostDto;
import com.example.petapp.domain.post.recommend.model.dto.response.CreateRecommendRoutePostResponseDto;
import com.example.petapp.domain.post.recommend.model.dto.response.GetRecommendPostResponseDto;
import com.example.petapp.domain.post.recommend.model.dto.response.GetRecommendRoutePostsResponseDto;
import com.example.petapp.domain.post.recommend.model.entity.RecommendRoutePost;
import com.example.petapp.port.InMemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecommendRoutePostServiceImpl implements RecommendRoutePostService {

    private final RecommendRoutePostRepository recommendRoutePostRepository;
    private final LikeRepository likeRepository;
    private final LikeService likeService;
    private final MemberQueryUseCase memberQueryUseCase;
    private final InMemoryService inMemoryService;
    private final PostQueryUseCase<RecommendRoutePost> postQueryUseCase;
    private final PostRepository<RecommendRoutePost> postRepository;

    @Transactional
    @Override
    public CreateRecommendRoutePostResponseDto createRecommendRoutePost(CreateRecommendRoutePostDto createRecommendRoutePostDto, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        RecommendRoutePost recommendRoutePost = RecommendRoutePostMapper.toEntity(createRecommendRoutePostDto, member);
        RecommendRoutePost savedRecommendRoutePost = recommendRoutePostRepository.save(recommendRoutePost);
        return new CreateRecommendRoutePostResponseDto(savedRecommendRoutePost.getId());
    }

    @Transactional(readOnly = true)//페이징 처리를 해야됨. 40개 정도 내보내면 프론트가 페이지 처리할 수 있으려나?
    @Override
    public List<GetRecommendRoutePostsResponseDto> getRecommendRoutePosts(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, int page, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        Pageable pageable = PageRequest.of(page - 1, 10);
        Set<Long> memberIds = inMemoryService.getLikeData(member.getId());
        List<RecommendRoutePost> recommendRoutePosts = recommendRoutePostRepository
                .findByRecommendRoutePostByLocation(minLongitude - 0.01, minLatitude - 0.01, maxLongitude + 0.01, maxLatitude + 0.01, pageable)
                .getContent();
        return RecommendRoutePostMapper.toRecommendRoutePostsList(recommendRoutePosts, likeService.getLikeCountMap(recommendRoutePosts), memberIds, member);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetRecommendRoutePostsResponseDto> getRecommendRoutePosts(Double longitude, Double latitude, int page, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        Pageable pageable = PageRequest.of(page - 1, 10);
        Set<Long> memberIds = inMemoryService.getLikeData(member.getId());
        List<RecommendRoutePost> recommendRoutePosts = recommendRoutePostRepository.findByRecommendRoutePostByPlace(longitude, latitude, pageable).getContent();
        return RecommendRoutePostMapper.toRecommendRoutePostsList(recommendRoutePosts, likeService.getLikeCountMap(recommendRoutePosts), memberIds, member);
    }

    @Transactional()
    @Override
    public GetRecommendPostResponseDto getRecommendRoutePost(Long recommendRoutePostId, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        RecommendRoutePost recommendRoutePost = postQueryUseCase.findOrThrow(recommendRoutePostId);
        recommendRoutePostRepository.incrementViewCount(recommendRoutePostId);
        return RecommendRoutePostMapper.toGetRecommendPostResponseDto(member, recommendRoutePost, likeRepository.countByPost(recommendRoutePost), likeRepository.existsByPostAndMember(recommendRoutePost, member));
    }

    @Transactional
    @Override
    public void updateRecommendRoutePost(Long recommendRoutePostId, UpdateRecommendRoutePostDto updateRecommendRoutePostDto, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        RecommendRoutePost recommendRoutePost = postQueryUseCase.findOrThrow(recommendRoutePostId);
        recommendRoutePost.validateMember(member);
        recommendRoutePost.updateContent(updateRecommendRoutePostDto.getTitle(), updateRecommendRoutePostDto.getContent());
    }

    @Transactional
    @Override
    public void deleteRecommendRoutePost(Long recommendRoutePostId, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        RecommendRoutePost recommendRoutePost = postQueryUseCase.findOrThrow(recommendRoutePostId);
        recommendRoutePost.validateMember(member);
        postRepository.delete(recommendRoutePostId);
    }
}
