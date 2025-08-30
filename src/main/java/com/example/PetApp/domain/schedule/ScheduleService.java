package com.example.PetApp.domain.schedule;

import com.example.PetApp.domain.schedule.model.dto.response.GetSchedulesResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ScheduleService {

    List<GetSchedulesResponseDto> getSchedules(String start, String finish, Long profileId);
}
