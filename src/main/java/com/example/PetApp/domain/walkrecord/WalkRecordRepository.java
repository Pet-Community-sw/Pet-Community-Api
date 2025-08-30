package com.example.PetApp.domain.walkrecord;

import com.example.PetApp.domain.walkrecord.model.entity.WalkRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WalkRecordRepository extends JpaRepository<WalkRecord, Long> {
}
