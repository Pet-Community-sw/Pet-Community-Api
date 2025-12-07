package com.example.petapp.application.in.walkrecord.mapper;

import com.example.petapp.application.in.walkrecord.dto.response.GetWalkRecordResponseDto;
import com.example.petapp.common.base.util.CreatedAtUtil;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.model.DelegateWalkPost;
import com.example.petapp.domain.walkrecord.model.WalkRecord;

public class WalkRecordMapper {

    public static WalkRecord toEntity(DelegateWalkPost delegateWalkPost, Member member) {
        return WalkRecord.builder()
                .walkStatus(WalkRecord.WalkStatus.READY)
                .delegateWalkPost(delegateWalkPost)
                .member(member)
                .build();
    }

    public static GetWalkRecordResponseDto toGetWalkRecordResponseDto(WalkRecord walkRecord) {
        return GetWalkRecordResponseDto.builder()
                .walkRecordId(walkRecord.getId())
                .startTime(walkRecord.getStartTime())
                .finishTime(walkRecord.getFinishTime())
                .walkTime(CreatedAtUtil.createdAt(walkRecord.getStartTime(), walkRecord.getFinishTime()))
                .walkDistance(walkRecord.getWalkDistance())
                .pathPoints(walkRecord.getPathPoints())
                .build();
    }
}
