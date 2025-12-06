package com.example.petapp.domain.walkrecord;

import com.example.petapp.domain.post.model.DelegateWalkPost;
import com.example.petapp.domain.walklocation.model.dto.response.GetWalkRecordLocationResponseDto;
import com.example.petapp.domain.walkrecord.model.dto.response.CreateWalkRecordResponseDto;
import com.example.petapp.domain.walkrecord.model.dto.response.GetWalkRecordResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface WalkRecordService {
    CreateWalkRecordResponseDto createWalkRecord(DelegateWalkPost delegateWalkPost);

    GetWalkRecordResponseDto getWalkRecord(Long walkRecordId, String email);

    void updateStartWalkRecord(Long walkRecordId, String email);

    void FinishWalkRecord(Long walkRecordId, String email);

    GetWalkRecordLocationResponseDto getWalkRecordLocation(Long walkRecordId, String email);

}