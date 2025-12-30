package com.example.petapp.application.in.walkrecord;

import com.example.petapp.application.in.walkrecord.dto.response.CreateWalkRecordResponseDto;
import com.example.petapp.application.in.walkrecord.dto.response.GetWalkRecordLocationResponseDto;
import com.example.petapp.application.in.walkrecord.dto.response.GetWalkRecordResponseDto;
import com.example.petapp.domain.post.model.DelegateWalkPost;

public interface WalkRecordUseCase {
    CreateWalkRecordResponseDto createWalkRecord(DelegateWalkPost delegateWalkPost);

    GetWalkRecordResponseDto getWalkRecord(Long walkRecordId, Long id);

    void updateStartWalkRecord(Long walkRecordId, Long id);

    void finishWalkRecord(Long walkRecordId, Long id);

    GetWalkRecordLocationResponseDto getWalkRecordLocation(Long walkRecordId, Long id);

}