package com.example.petapp.application.in.schedule;

import com.example.petapp.domain.schedule.model.dto.response.GetSchedulesResponseDto;

import java.util.List;

public interface ScheduleUseCase {
    List<GetSchedulesResponseDto> getSchedules(String start, String finish, Long profileId);
}