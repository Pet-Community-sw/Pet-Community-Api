package com.example.petapp.application.in.walkrecord.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendLocationDto {
    Double locationLongitude;
    Double locationLatitude;
    Double walkerLongitude;
    Double walkerLatitude;
}
