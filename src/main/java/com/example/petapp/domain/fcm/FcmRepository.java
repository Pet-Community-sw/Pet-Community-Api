package com.example.petapp.domain.fcm;

import com.example.petapp.domain.fcm.model.FcmToken;

public interface FcmRepository {

    void save(FcmToken fcmToken);
}