package com.example.PetApp.domain.walkrecord.model.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LocationMessage {
    private Long walkRecordId;

    private Double longitude;

    private Double latitude;
}
