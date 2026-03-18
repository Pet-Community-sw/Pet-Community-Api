package com.example.petapp.application.in.match.mapper;

import com.example.petapp.application.common.TimeUtil;
import com.example.petapp.application.in.match.dto.request.CreateWalkingTogetherPostDto;
import com.example.petapp.application.in.match.dto.response.GetWalkingTogetherPostResponseDto;
import com.example.petapp.domain.petbreed.model.PetBreed;
import com.example.petapp.domain.post.model.RecommendRoutePost;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;

import java.util.List;
import java.util.stream.Collectors;

public class WalkingTogetherPostMapper {

    public static WalkingTogetherPost toEntity(Profile profile, RecommendRoutePost recommendRoutePost, CreateWalkingTogetherPostDto createWalkingTogetherPostDto) {
        return WalkingTogetherPost.builder()
                .profile(profile)
                .recommendRoutePost(recommendRoutePost)
                .scheduledTime(createWalkingTogetherPostDto.getScheduledTime())
                .limitCount(createWalkingTogetherPostDto.getLimitCount())
                .build();
    }


    public static List<GetWalkingTogetherPostResponseDto> toGetWalkingTogetherPostResponseDtos(List<WalkingTogetherPost> walkingTogetherPosts,
                                                                                               PetBreed petBreed) {
        return walkingTogetherPosts.stream()
                .map(walkingTogetherPost -> toGetWalkingTogetherPostResponseDto
                        (walkingTogetherPost.getId(),
                                walkingTogetherPost,
                                walkingTogetherPost.getProfile(),
                                petBreed
                        )).collect(Collectors.toList());
    }

    public static GetWalkingTogetherPostResponseDto toGetWalkingTogetherPostResponseDto(Long walkingTogetherPostId,
                                                                                        WalkingTogetherPost walkingTogetherPost,
                                                                                        Profile profile,
                                                                                        PetBreed petBreed) {
        return GetWalkingTogetherPostResponseDto.builder()
                .walkingTogetherPostId(walkingTogetherPostId)
                .petName(walkingTogetherPost.getProfile().getPetName())
                .petImageUrl(walkingTogetherPost.getProfile().getPetImageUrl())
                .scheduledTime(walkingTogetherPost.getScheduledTime())
                .currentCount(walkingTogetherPost.getProfiles().size())
                .limitCount(walkingTogetherPost.getLimitCount())
                .createdAt(TimeUtil.getTimeAgo(walkingTogetherPost.getCreatedAt()))
                .isOwner(walkingTogetherPost.getProfile().equals(profile))
                .filtering(walkingTogetherPost.getAvoidBreeds().contains(petBreed.getId()))//true이면 신청불가
                .build();
    }
}
