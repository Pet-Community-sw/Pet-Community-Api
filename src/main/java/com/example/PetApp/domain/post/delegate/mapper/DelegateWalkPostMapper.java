package com.example.PetApp.domain.post.delegate.mapper;

import com.example.PetApp.domain.post.delegate.model.entity.DelegateWalkPost;
import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.infrastructure.database.base.embedded.Location;
import com.example.PetApp.infrastructure.database.base.embedded.Content;
import com.example.PetApp.domain.post.delegate.model.dto.request.CreateDelegateWalkPostDto;
import com.example.PetApp.domain.post.delegate.model.dto.response.GetDelegateWalkPostsResponseDto;
import com.example.PetApp.domain.post.delegate.model.dto.request.GetPostResponseDto;
import com.example.PetApp.common.util.TimeAgoUtil;

import java.util.List;
import java.util.stream.Collectors;

public class DelegateWalkPostMapper {

    public static DelegateWalkPost toEntity(CreateDelegateWalkPostDto createDelegateWalkPostDto, Profile profile) {
        return DelegateWalkPost.builder()
                .content(new Content(createDelegateWalkPostDto.getTitle(), createDelegateWalkPostDto.getContent()))
                .price(createDelegateWalkPostDto.getPrice())
                .location(new Location(createDelegateWalkPostDto.getLocationLongitude(), createDelegateWalkPostDto.getLocationLatitude()))
                .allowedRadiusMeters(createDelegateWalkPostDto.getAllowedRadiusMeters())
                .scheduledTime(createDelegateWalkPostDto.getScheduledTime())
                .profile(profile)
                .requireProfile(createDelegateWalkPostDto.isRequireProfile())
                .build();
    }

    public static List<GetDelegateWalkPostsResponseDto> toGetDelegateWalkPostsResponseDtos(Member member, List<DelegateWalkPost> delegateWalkPosts) {
        return delegateWalkPosts.stream()
                .map(delegateWalkPost -> GetDelegateWalkPostsResponseDto.builder()
                        .delegateWalkPostId(delegateWalkPost.getId())
                        .profileId(delegateWalkPost.getProfile().getId())
                        .petName(delegateWalkPost.getProfile().getPetName())
                        .petImageUrl(delegateWalkPost.getProfile().getPetImageUrl())
                        .title(delegateWalkPost.getContent().getTitle())
                        .price(delegateWalkPost.getPrice())
                        .locationLongitude(delegateWalkPost.getLocation().getLocationLongitude())
                        .locationLatitude(delegateWalkPost.getLocation().getLocationLatitude())
                        .scheduledTime(delegateWalkPost.getScheduledTime())
                        .filtering(delegateWalkPost.filtering(member))
                        .applicantCount(delegateWalkPost.getApplicants().size())
                        .createdAt(TimeAgoUtil.getTimeAgo(delegateWalkPost.getCreatedAt()))
                        .build())
                .collect(Collectors.toList());
    }

    public static GetPostResponseDto toGetPostResponseDto(DelegateWalkPost delegateWalkPost) {
        return GetPostResponseDto.builder()
                .delegateWalkPostId(delegateWalkPost.getId())
                .title(delegateWalkPost.getContent().getTitle())
                .content(delegateWalkPost.getContent().getContent())
                .price(delegateWalkPost.getPrice())
                .locationLongitude(delegateWalkPost.getLocation().getLocationLongitude())
                .locationLatitude(delegateWalkPost.getLocation().getLocationLatitude())
                .allowedRadiusMeters(delegateWalkPost.getAllowedRadiusMeters())
                .scheduledTime(delegateWalkPost.getScheduledTime())
                .petName(delegateWalkPost.getProfile().getPetName())
                .petImageUrl(delegateWalkPost.getProfile().getPetImageUrl())
                .petBreed(String.valueOf(delegateWalkPost.getProfile().getPetBreed()))
                .extraInfo(delegateWalkPost.getProfile().getExtraInfo())
                .applicantCount(delegateWalkPost.getApplicants().size())
                .createdAt(TimeAgoUtil.getTimeAgo(delegateWalkPost.getCreatedAt()))
                .build();
    }

}
