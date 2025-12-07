package com.example.petapp.application.in.walkrecord;

import com.example.petapp.application.in.walkrecord.dto.response.CreateWalkRecordResponseDto;
import com.example.petapp.application.in.walkrecord.dto.response.GetWalkRecordResponseDto;
import com.example.petapp.domain.post.model.DelegateWalkPost;
import com.example.petapp.domain.walklocation.model.dto.response.GetWalkRecordLocationResponseDto;

public interface WalkRecordUseCase {
    CreateWalkRecordResponseDto createWalkRecord(DelegateWalkPost delegateWalkPost);

    GetWalkRecordResponseDto getWalkRecord(Long walkRecordId, String email);

    void updateStartWalkRecord(Long walkRecordId, String email);

    void FinishWalkRecord(Long walkRecordId, String email);

    GetWalkRecordLocationResponseDto getWalkRecordLocation(Long walkRecordId, String email);

}