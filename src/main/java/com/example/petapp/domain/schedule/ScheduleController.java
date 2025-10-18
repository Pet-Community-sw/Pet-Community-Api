package com.example.petapp.domain.schedule;


import com.example.petapp.common.base.util.AuthUtil;
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

@Tag(name = "Schedule")
@RequiredArgsConstructor
@RestController()
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(
            summary = "일정 목록 조회"
    )
    @GetMapping()
    public List<GetSchedulesResponseDto> getSchedules(@RequestParam String start, @RequestParam String end, Authentication authentication) {
        return scheduleService.getSchedules(start, end, AuthUtil.getProfileId(authentication));
    }
}
