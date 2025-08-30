package com.example.PetApp.domain.walkrecord;

import com.example.PetApp.domain.post.delegate.model.entity.DelegateWalkPost;
import com.example.PetApp.domain.walkrecord.model.dto.response.CreateWalkRecordResponseDto;
import com.example.PetApp.domain.walkrecord.model.dto.response.GetWalkRecordLocationResponseDto;
import com.example.PetApp.domain.walkrecord.model.dto.response.GetWalkRecordResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface WalkRecordService {
    CreateWalkRecordResponseDto createWalkRecord(DelegateWalkPost delegateWalkPost);

    GetWalkRecordResponseDto getWalkRecord(Long walkRecordId, String email);

    void updateStartWalkRecord(Long walkRecordId, String email);

    void updateFinishWalkRecord(Long walkRecordId, String email);

    GetWalkRecordLocationResponseDto getWalkRecordLocation(Long walkRecordId, String email);

}