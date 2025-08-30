package com.example.PetApp.domain.fcm;

import com.example.PetApp.domain.fcm.model.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

}
