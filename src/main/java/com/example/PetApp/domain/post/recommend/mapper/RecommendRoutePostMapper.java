package com.example.PetApp.domain.post.recommend.mapper;

import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.post.recommend.model.entity.RecommendRoutePost;
import com.example.PetApp.infrastructure.database.base.embedded.Location;
import com.example.PetApp.infrastructure.database.base.embedded.Content;
import com.example.PetApp.domain.post.recommend.model.dto.request.CreateRecommendRoutePostDto;
import com.example.PetApp.domain.post.recommend.model.dto.response.GetRecommendPostResponseDto;
import com.example.PetApp.domain.post.recommend.model.dto.response.GetRecommendRoutePostsResponseDto;
import com.example.PetApp.common.util.TimeAgoUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RecommendRoutePostMapper {
    public static RecommendRoutePost  toEntity(CreateRecommendRoutePostDto createRecommendRoutePostDto, Member member) {
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
                        .locationLongitude(recommendRoutePost.getLocation().getLocationLongitude())
                        .locationLatitude(recommendRoutePost.getLocation().getLocationLatitude())
                        .createdAt(TimeAgoUtil.getTimeAgo(recommendRoutePost.getCreatedAt()))
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
                .createdAt(TimeAgoUtil.getTimeAgo(post.getCreatedAt()))
                .likeCount(likeCount)
                .isOwner(post.getMember().getId().equals(member.getId()))
                .isLike(isLike)
                .build();
    }
}
