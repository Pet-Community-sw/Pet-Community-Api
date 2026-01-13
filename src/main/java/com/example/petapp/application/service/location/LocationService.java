package com.example.petapp.application.service.location;

import com.example.petapp.application.in.location.LocationUseCase;
import com.example.petapp.application.in.location.dto.request.LocationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService implements LocationUseCase {//예외 처리해야됨.

    private final LocationPipeline locationPipeline;

    @Override
    public void sendLocation(LocationMessage message, String memberId) {
        locationPipeline.send(message, memberId);
    }

    @Override
    public void finishWalkRecord(Long walkRecordId) {
        locationPipeline.clean(walkRecordId);
    }
}
