package com.example.PetApp.service.post.recommend;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.post.RecommendRoutePost;
import com.example.PetApp.domain.embedded.Content;
import com.example.PetApp.dto.recommendroutepost.*;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.mapper.RecommendRoutePostMapper;
import com.example.PetApp.repository.jpa.LikeRepository;
import com.example.PetApp.repository.jpa.RecommendRoutePostRepository;
import com.example.PetApp.service.like.LikeService;
import com.example.PetApp.service.query.QueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecommendRoutePostServiceImpl implements RecommendRoutePostService{

    private final RecommendRoutePostRepository recommendRoutePostRepository;
    private final LikeRepository likeRepository;
    private final LikeService likeService;
    private final QueryService queryService;
    private final RedisTemplate<String, Long> likeRedisTemplate;

    @Transactional
    @Override
    public CreateRecommendRoutePostResponseDto createRecommendRoutePost(CreateRecommendRoutePostDto createRecommendRoutePostDto, String email) {
        Member member = queryService.findByMember(email);
        RecommendRoutePost recommendRoutePost = RecommendRoutePostMapper.toEntity(createRecommendRoutePostDto, member);
        RecommendRoutePost savedRecommendRoutePost = recommendRoutePostRepository.save(recommendRoutePost);
        return new CreateRecommendRoutePostResponseDto(savedRecommendRoutePost.getId());
    }

    @Transactional(readOnly = true)//페이징 처리를 해야됨. 40개 정도 내보내면 프론트가 페이지 처리할 수 있으려나?
    @Override
    public List<GetRecommendRoutePostsResponseDto> getRecommendRoutePosts(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, int page, String email) {
        Member member = queryService.findByMember(email);
        Pageable pageable = PageRequest.of(page, 10);
        Set<Long> memberIds = likeRedisTemplate.opsForSet().members("member:likes:" + member.getId());
        List<RecommendRoutePost> recommendRoutePosts = recommendRoutePostRepository
                .findByRecommendRoutePostByLocation(minLongitude - 0.01, minLatitude - 0.01, maxLongitude + 0.01, maxLatitude + 0.01, pageable)
                .getContent();
        return RecommendRoutePostMapper.toRecommendRoutePostsList(recommendRoutePosts, likeService.getLikeCountMap(recommendRoutePosts), memberIds, member);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetRecommendRoutePostsResponseDto> getRecommendRoutePosts(Double longitude, Double latitude, int page, String email) {
        Member member = queryService.findByMember(email);
        Pageable pageable = PageRequest.of(page, 10);
        Set<Long> memberIds = likeRedisTemplate.opsForSet().members("member:likes:" + member.getId());
        List<RecommendRoutePost> recommendRoutePosts = recommendRoutePostRepository.findByRecommendRoutePostByPlace(longitude, latitude, pageable).getContent();

        return RecommendRoutePostMapper.toRecommendRoutePostsList(recommendRoutePosts, likeService.getLikeCountMap(recommendRoutePosts), memberIds, member);
    }

    @Transactional(readOnly = true)
    @Override
    public GetRecommendPostResponseDto getRecommendRoutePost(Long recommendRoutePostId, String email) {
        Member member = queryService.findByMember(email);
        RecommendRoutePost recommendRoutePost = queryService.findByRecommendRoutePost(recommendRoutePostId);
        return RecommendRoutePostMapper.toGetRecommendPostResponseDto(member, recommendRoutePost, likeRepository.countByPost(recommendRoutePost), likeRepository.existsByPostAndMember(recommendRoutePost, member));
    }

    @Transactional
    @Override
    public void updateRecommendRoutePost(Long recommendRoutePostId, UpdateRecommendRoutePostDto updateRecommendRoutePostDto, String email) {
        Member member = queryService.findByMember(email);
        RecommendRoutePost recommendRoutePost = queryService.findByRecommendRoutePost(recommendRoutePostId);
        validateMember(recommendRoutePost, member);
        recommendRoutePost.setContent(new Content(updateRecommendRoutePostDto.getTitle(), updateRecommendRoutePostDto.getContent()));
    }

    @Transactional
    @Override
    public void deleteRecommendRoutePost(Long recommendRoutePostId, String email) {
        Member member = queryService.findByMember(email);
        RecommendRoutePost recommendRoutePost = queryService.findByRecommendRoutePost(recommendRoutePostId);
        validateMember(recommendRoutePost, member);
        recommendRoutePostRepository.deleteById(recommendRoutePostId);
    }

    private static void validateMember(RecommendRoutePost recommendRoutePost, Member member) {
        if (!(recommendRoutePost.getMember().equals(member))) {
            throw new ForbiddenException("권한이 없습니다.");
        }
    }

}
