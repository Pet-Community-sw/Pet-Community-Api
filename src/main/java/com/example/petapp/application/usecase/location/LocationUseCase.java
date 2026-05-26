package com.example.petapp.application.usecase.location;

import com.example.petapp.application.usecase.location.dto.request.LocationMessage;

public interface LocationUseCase {
    void sendLocation(LocationMessage locationMessage, String memberId);

    void finishWalkRecord(Long walkRecordId);
}
