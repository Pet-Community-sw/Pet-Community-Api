package com.example.petapp.application.in.location;

import com.example.petapp.application.in.location.dto.request.LocationMessage;
import com.example.petapp.application.service.location.object.WalkRangeStatus;
import com.example.petapp.domain.walkrecord.model.WalkRecord;

public interface LocationProcessorUseCase {
    void sendNotification(WalkRecord walkRecord, WalkRangeStatus status);

    WalkRangeStatus checkRange(WalkRecord walkRecord, LocationMessage message);

    void saveAndBroadcast(LocationMessage message);

    boolean isEnoughMove(LocationMessage message);

    void clean(Long walkRecordId);
}
