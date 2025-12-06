package com.example.petapp.domain.walkingtogethermatch.mapper;

import com.example.petapp.common.base.util.TimeUtil;
import com.example.petapp.domain.petbreed.model.entity.PetBreed;
import com.example.petapp.domain.post.recommend.model.entity.RecommendRoutePost;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.walkingtogethermatch.model.dto.request.CreateWalkingTogetherMatchDto;
import com.example.petapp.domain.walkingtogethermatch.model.dto.response.GetWalkingTogetherMatchResponseDto;
import com.example.petapp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;

import java.util.List;
import java.util.stream.Collectors;

public class WalkingTogetherMatchMapper {

    public static WalkingTogetherMatch toEntity(Profile profile, RecommendRoutePost recommendRoutePost, CreateWalkingTogetherMatchDto createWalkingTogetherMatchDto) {
        return WalkingTogetherMatch.builder()
                .profile(profile)
                .recommendRoutePost(recommendRoutePost)
                .scheduledTime(createWalkingTogetherMatchDto.getScheduledTime())
                .limitCount(createWalkingTogetherMatchDto.getLimitCount())
                .build();
    }


    public static List<GetWalkingTogetherMatchResponseDto> toGetWalkingTogetherPostResponseDtos(List<WalkingTogetherMatch> walkingTogetherMatches,
                                                                                                PetBreed petBreed) {
        return walkingTogetherMatches.stream()
                .map(walkingTogetherPost -> toGetWalkingTogetherPostResponseDto
                        (walkingTogetherPost.getId(),
                                walkingTogetherPost,
                                walkingTogetherPost.getProfile(),
                                petBreed
                        )).collect(Collectors.toList());
    }

    public static GetWalkingTogetherMatchResponseDto toGetWalkingTogetherPostResponseDto(Long walkingTogetherPostId,
                                                                                         WalkingTogetherMatch walkingTogetherMatch,
                                                                                         Profile profile,
                                                                                         PetBreed petBreed) {
        return GetWalkingTogetherMatchResponseDto.builder()
                .walkingTogetherPostId(walkingTogetherPostId)
                .petName(walkingTogetherMatch.getProfile().getPetName())
                .petImageUrl(walkingTogetherMatch.getProfile().getPetImageUrl())
                .scheduledTime(walkingTogetherMatch.getScheduledTime())
                .currentCount(walkingTogetherMatch.getProfiles().size())
                .limitCount(walkingTogetherMatch.getLimitCount())
                .createdAt(TimeUtil.getTimeAgo(walkingTogetherMatch.getCreatedAt()))
                .isOwner(walkingTogetherMatch.getProfile().equals(profile))
                .filtering(walkingTogetherMatch.getAvoidBreeds().contains(petBreed.getId()))//true이면 신청불가
                .build();
    }
}
