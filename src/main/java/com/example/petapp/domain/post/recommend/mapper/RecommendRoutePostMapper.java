package com.example.petapp.domain.post.recommend.mapper;

import com.example.petapp.common.base.embedded.Content;
import com.example.petapp.common.base.embedded.Location;
import com.example.petapp.common.base.util.TimeUtil;
import com.example.petapp.domain.member.model.entity.Member;
import com.example.petapp.domain.post.recommend.model.dto.request.CreateRecommendRoutePostDto;
import com.example.petapp.domain.post.recommend.model.dto.response.GetRecommendPostResponseDto;
import com.example.petapp.domain.post.recommend.model.dto.response.GetRecommendRoutePostsResponseDto;
import com.example.petapp.domain.post.recommend.model.entity.RecommendRoutePost;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RecommendRoutePostMapper {
    public static RecommendRoutePost toEntity(CreateRecommendRoutePostDto createRecommendRoutePostDto, Member member) {
        return RecommendRoutePost.builder()
                .content(new Content(createRecommendRoutePostDto.getTitle(), createRecommendRoutePostDto.getContent()))
                .location(new Location(createRecommendRoutePostDto.getLocationLongitude(), createRecommendRoutePostDto.getLocationLatitude()))
                .member(member)
                .build();
    }

    public static List<GetRecommendRoutePostsResponseDto> toRecommendRoutePostsList(List<RecommendRoutePost> recommendRoutePosts,
                                                                                    Map<Long, Long> likeCountMap,
                                                                                    Set<Long> likedRecommendPostIds,
                                                                                    Member member) {
        return recommendRoutePosts.stream()
                .map(recommendRoutePost -> GetRecommendRoutePostsResponseDto.builder()
                        .recommendRoutePostId(recommendRoutePost.getId())
                        .title(recommendRoutePost.getContent().getTitle())
                        .memberId(recommendRoutePost.getMember().getId())
                        .memberName(recommendRoutePost.getMember().getName())
                        .memberImageUrl(recommendRoutePost.getMember().getMemberImageUrl())
                        .likeCount(likeCountMap.getOrDefault(recommendRoutePost.getId(), 0L))
                        .viewCount(recommendRoutePost.getViewCount())
                        .locationLongitude(recommendRoutePost.getLocation().getLocationLongitude())
                        .locationLatitude(recommendRoutePost.getLocation().getLocationLatitude())
                        .createdAt(TimeUtil.getTimeAgo(recommendRoutePost.getCreatedAt()))
                        .isOwner(member.getId().equals(recommendRoutePost.getMember().getId()))
                        .isLike(likedRecommendPostIds.contains(recommendRoutePost.getId()))
                        .build()
                )
                .collect(Collectors.toList());
    }

    public static GetRecommendPostResponseDto toGetRecommendPostResponseDto(Member member, RecommendRoutePost post, Long likeCount, boolean isLike) {
        return GetRecommendPostResponseDto.builder()
                .recommendRoutePostId(post.getId())
                .title(post.getContent().getTitle())
                .content(post.getContent().getContent())
                .memberId(post.getMember().getId())
                .memberName(post.getMember().getName())
                .memberImageUrl(post.getMember().getMemberImageUrl())
                .createdAt(TimeUtil.getTimeAgo(post.getCreatedAt()))
                .viewCount(post.getViewCount())
                .likeCount(likeCount)
                .isOwner(post.getMember().getId().equals(member.getId()))
                .isLike(isLike)
                .build();
    }
}
