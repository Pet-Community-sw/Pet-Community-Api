package com.example.petapp.domain.post.delegate.mapper;

import com.example.petapp.common.base.embedded.Content;
import com.example.petapp.common.base.embedded.Location;
import com.example.petapp.common.base.util.TimeUtil;
import com.example.petapp.domain.member.model.entity.Member;
import com.example.petapp.domain.post.delegate.model.dto.request.CreateDelegateWalkPostDto;
import com.example.petapp.domain.post.delegate.model.dto.request.GetPostResponseDto;
import com.example.petapp.domain.post.delegate.model.dto.response.GetDelegateWalkPostsResponseDto;
import com.example.petapp.domain.post.delegate.model.entity.DelegateWalkPost;
import com.example.petapp.domain.profile.model.entity.Profile;

import java.util.List;
import java.util.stream.Collectors;

public class DelegateWalkPostMapper {

    public static DelegateWalkPost toEntity(CreateDelegateWalkPostDto createDelegateWalkPostDto, Profile profile) {
        return DelegateWalkPost.builder()
                .member(profile.getMember())
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
                        .createdAt(TimeUtil.getTimeAgo(delegateWalkPost.getCreatedAt()))
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
                .createdAt(TimeUtil.getTimeAgo(delegateWalkPost.getCreatedAt()))
                .build();
    }
}
