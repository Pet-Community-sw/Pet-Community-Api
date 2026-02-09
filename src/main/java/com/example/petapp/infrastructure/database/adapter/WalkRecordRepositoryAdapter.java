package com.example.petapp.infrastructure.database.adapter;

import com.example.petapp.domain.walkrecord.WalkRecordRepository;
import com.example.petapp.domain.walkrecord.model.WalkRecord;
import com.example.petapp.infrastructure.database.jpa.walkrecord.JpaWalkRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WalkRecordRepositoryAdapter implements WalkRecordRepository {

    private final JpaWalkRecordRepository repository;

    @Override
    public WalkRecord save(WalkRecord walkRecord) {
        return repository.save(walkRecord);
    }

    @Override
    public Optional<WalkRecord> find(Long id) {
        return repository.findById(id);
    }
}
