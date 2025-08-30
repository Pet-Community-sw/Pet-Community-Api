package com.example.PetApp.domain.walkrecord.mapper;

import com.example.PetApp.domain.walkrecord.model.entity.WalkRecord;
import com.example.PetApp.domain.walkrecord.model.dto.request.SendLocationDto;
import com.example.PetApp.domain.walkrecord.model.dto.request.LocationMessage;

public class LocationMapper {

    public static SendLocationDto toSendLocationDto(WalkRecord walkRecord, LocationMessage locationMessage) {
        return SendLocationDto.builder()
                .locationLongitude(walkRecord.getDelegateWalkPost().getLocation().getLocationLongitude())
                .locationLatitude(walkRecord.getDelegateWalkPost().getLocation().getLocationLatitude())
                .walkerLongitude(locationMessage.getLongitude())
                .walkerLatitude(locationMessage.getLatitude())
                .build();

    }
}
