package com.example.petapp.domain.fcm;

import com.example.petapp.domain.fcm.model.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

}
