package com.example.petapp.domain.walklocation.mapper;

import com.example.petapp.domain.walklocation.model.dto.request.LocationMessage;
import com.example.petapp.domain.walkrecord.model.dto.request.SendLocationDto;
import com.example.petapp.domain.walkrecord.model.entity.WalkRecord;

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
