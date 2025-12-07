package com.example.petapp.infrastructure.database.jpa.walkrecord;

import com.example.petapp.domain.walkrecord.WalkRecordRepository;
import com.example.petapp.domain.walkrecord.model.WalkRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WalkRecordRepositoryImpl implements WalkRecordRepository {

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
