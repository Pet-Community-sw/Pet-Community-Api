package com.example.petapp.infrastructure.database.jpa.walkrecord;

import com.example.petapp.domain.walkrecord.model.WalkRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaWalkRecordRepository extends JpaRepository<WalkRecord, Long> {
}
