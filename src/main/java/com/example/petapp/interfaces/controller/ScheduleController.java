package com.example.petapp.interfaces.controller;


import com.example.petapp.application.common.AuthUtil;
import com.example.petapp.application.in.schedule.ScheduleUseCase;
import com.example.petapp.domain.schedule.model.dto.response.GetSchedulesResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Schedules")
@RequiredArgsConstructor
@RestController()
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleUseCase scheduleUseCase;

    @Operation(
            summary = "일정 목록 조회"
    )
    @GetMapping()
    public List<GetSchedulesResponseDto> getSchedules(@RequestParam String start, @RequestParam String end, Authentication authentication) {
        return scheduleUseCase.getSchedules(start, end, AuthUtil.getProfileId(authentication));
    }
}
