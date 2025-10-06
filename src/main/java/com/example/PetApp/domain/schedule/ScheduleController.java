package com.example.PetApp.domain.schedule;


import com.example.PetApp.domain.schedule.model.dto.response.GetSchedulesResponseDto;
import com.example.PetApp.common.base.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping()
    public List<GetSchedulesResponseDto> getSchedules(@RequestParam String start, @RequestParam String end, Authentication authentication) {
        return scheduleService.getSchedules(start, end, AuthUtil.getProfileId(authentication));
    }

}
