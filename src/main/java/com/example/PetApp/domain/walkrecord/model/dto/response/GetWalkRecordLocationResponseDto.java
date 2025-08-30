package com.example.PetApp.domain.walkrecord.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetWalkRecordLocationResponseDto {

    private String lastLocation;
}
