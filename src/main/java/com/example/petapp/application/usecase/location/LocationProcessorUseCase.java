package com.example.petapp.application.usecase.location;

import com.example.petapp.application.service.location.object.WalkRangeStatus;
import com.example.petapp.application.usecase.location.dto.request.LocationMessage;
import com.example.petapp.domain.walkrecord.model.WalkRecord;

public interface LocationProcessorUseCase {
    void sendNotification(WalkRecord walkRecord, WalkRangeStatus status);

    WalkRangeStatus checkRange(WalkRecord walkRecord, LocationMessage message);

    void saveAndSend(LocationMessage message);

    boolean isEnoughMove(LocationMessage message);

    void clean(Long walkRecordId);
}
