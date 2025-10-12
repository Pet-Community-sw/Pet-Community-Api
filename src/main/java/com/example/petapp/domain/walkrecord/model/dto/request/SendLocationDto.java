package com.example.petapp.domain.walkrecord.model.dto.request;

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
