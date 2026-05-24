package com.example.petapp.application.usecase.walkrecord.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetWalkRecordLocationResponseDto {

    private String lastLocation;
}
