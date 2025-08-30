package com.example.PetApp.domain.schedule.model.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeDto {

    private LocalDateTime start;
    private LocalDateTime end;
}
