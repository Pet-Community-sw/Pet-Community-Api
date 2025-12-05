package com.example.petapp.domain.walklocation.mapper;

import com.example.petapp.common.base.util.CreatedAtUtil;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.delegate.model.entity.DelegateWalkPost;
import com.example.petapp.domain.walkrecord.model.dto.response.GetWalkRecordResponseDto;
import com.example.petapp.domain.walkrecord.model.entity.WalkRecord;

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
