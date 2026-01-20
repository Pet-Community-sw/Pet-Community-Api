package com.example.petapp.infrastructure.database.jpa.fcm;

import com.example.petapp.domain.fcm.FcmRepository;
import com.example.petapp.domain.fcm.model.FcmToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FcmRepositoryImpl implements FcmRepository {
    private final JpaFcmRepository repository;


    @Override
    public void save(FcmToken fcmToken) {
        repository.save(fcmToken);
    }
}
