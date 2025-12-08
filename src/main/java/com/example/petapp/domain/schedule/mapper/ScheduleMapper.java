package com.example.petapp.domain.schedule.mapper;

import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.model.DelegateWalkPost;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.schedule.model.dto.response.GetSchedulesResponseDto;
import com.example.petapp.domain.schedule.model.dto.response.ScheduleType;
import com.example.petapp.domain.walkingtogethermatch.model.WalkingTogetherMatch;

public class ScheduleMapper {

    public static GetSchedulesResponseDto toGetSchedulesResponseDto(Profile profile, WalkingTogetherMatch walkingTogetherMatch) {
        return GetSchedulesResponseDto.builder()
                .memberId(profile.getMember().getId())
                .scheduleDate(walkingTogetherMatch.getScheduledTime())
                .scheduleType(ScheduleType.WALKING_TOGETHER)
                .build();
    }

    public static GetSchedulesResponseDto toGetSchedulesResponseDto(Member member, DelegateWalkPost delegateWalkPost) {
        return GetSchedulesResponseDto.builder()
                .memberId(member.getId())
                .scheduleDate(delegateWalkPost.getScheduledTime())
                .scheduleType(ScheduleType.DELEGATE_WALK)
                .build();
    }
}
