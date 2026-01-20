package com.example.petapp.infrastructure.database.jpa.fcm;

import com.example.petapp.domain.fcm.model.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaFcmRepository extends JpaRepository<FcmToken, Long> {
}
