package com.example.petapp.application.usecase.walkrecord.service;

import com.example.petapp.application.common.exception.ErrorCode;
import com.example.petapp.application.common.exception.PetCommunityException;
import com.example.petapp.application.usecase.walkrecord.WalkRecordQueryUseCase;
import com.example.petapp.domain.walkrecord.WalkRecordRepository;
import com.example.petapp.domain.walkrecord.model.WalkRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class WalkRecordQueryService implements WalkRecordQueryUseCase {

    private final WalkRecordRepository repository;

    @Override
    @Transactional(readOnly = true)
    public WalkRecord findOrThrow(Long id) {
        return repository.find(id).orElseThrow(() -> new PetCommunityException(ErrorCode.NOT_FOUND, "해당 산책기록은 없습니다."));
    }

    @Override
    @Transactional(readOnly = true)
    public WalkRecord findAndValidate(Long id, Long memberId) {
        WalkRecord walkRecord = findOrThrow(id);
        walkRecord.validateMember(memberId);
        walkRecord.validateStart();
        return walkRecord;
    }
}
