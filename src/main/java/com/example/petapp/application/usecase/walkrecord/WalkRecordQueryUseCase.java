package com.example.petapp.application.usecase.walkrecord;

import com.example.petapp.domain.walkrecord.model.WalkRecord;

public interface WalkRecordQueryUseCase {
    WalkRecord findOrThrow(Long id);

    WalkRecord findAndValidate(Long id, Long memberId);
}
