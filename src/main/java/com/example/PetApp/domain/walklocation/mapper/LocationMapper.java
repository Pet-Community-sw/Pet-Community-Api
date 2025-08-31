package com.example.PetApp.domain.walklocation.mapper;

import com.example.PetApp.domain.walklocation.model.dto.request.LocationMessage;
import com.example.PetApp.domain.walkrecord.model.dto.request.SendLocationDto;
import com.example.PetApp.domain.walkrecord.model.entity.WalkRecord;

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
