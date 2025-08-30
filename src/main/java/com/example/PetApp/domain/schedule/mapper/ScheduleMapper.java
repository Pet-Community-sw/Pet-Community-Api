package com.example.PetApp.domain.schedule.mapper;

import com.example.PetApp.domain.post.delegate.model.entity.DelegateWalkPost;
import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import com.example.PetApp.domain.schedule.model.dto.response.GetSchedulesResponseDto;
import com.example.PetApp.domain.schedule.model.dto.response.ScheduleType;

public class ScheduleMapper {

    public static GetSchedulesResponseDto toGetSchedulesResponseDto(Profile profile, WalkingTogetherMatch walkingTogetherMatch) {
        return GetSchedulesResponseDto.builder()
                .memberId(profile.getMember().getMemberId())
                .scheduleDate(walkingTogetherMatch.getScheduledTime())
                .scheduleType(ScheduleType.WALKING_TOGETHER)
                .build();
    }
    public static GetSchedulesResponseDto toGetSchedulesResponseDto(Member member, DelegateWalkPost delegateWalkPost) {
        return GetSchedulesResponseDto.builder()
                .memberId(member.getMemberId())
                .scheduleDate(delegateWalkPost.getScheduledTime())
                .scheduleType(ScheduleType.DELEGATE_WALK)
                .build();
    }}
