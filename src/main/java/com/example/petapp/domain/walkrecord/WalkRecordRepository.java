package com.example.petapp.domain.walkrecord;

import com.example.petapp.domain.walkrecord.model.WalkRecord;

import java.util.Optional;

public interface WalkRecordRepository {
    WalkRecord save(WalkRecord walkRecord);

    Optional<WalkRecord> find(Long id);
}
