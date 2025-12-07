package com.example.petapp.application.in.walkrecord;

import com.example.petapp.domain.walkrecord.model.WalkRecord;

public interface WalkRecordQueryUseCase {
    WalkRecord findOrThrow(Long id);
}
