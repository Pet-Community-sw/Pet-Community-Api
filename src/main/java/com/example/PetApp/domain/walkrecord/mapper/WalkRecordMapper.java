package com.example.PetApp.domain.walkrecord.mapper;

import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.walkrecord.model.entity.WalkRecord;
import com.example.PetApp.domain.post.delegate.model.entity.DelegateWalkPost;
import com.example.PetApp.domain.walkrecord.model.dto.response.GetWalkRecordResponseDto;
import com.example.PetApp.common.base.util.CreatedAtUtil;

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
                .walkTime(CreatedAtUtil.createdAt(walkRecord.getStartTime(),walkRecord.getFinishTime()))
                .walkDistance(walkRecord.getWalkDistance())
                .pathPoints(walkRecord.getPathPoints())
                .build();
    }
}
