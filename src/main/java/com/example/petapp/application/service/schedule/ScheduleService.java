package com.example.petapp.application.service.schedule;

import com.example.petapp.application.in.profile.ProfileUseCase;
import com.example.petapp.application.in.schedule.ScheduleUseCase;
import com.example.petapp.domain.member.MemberRepository;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.DelegateWalkPostRepository;
import com.example.petapp.domain.post.model.DelegateWalkPost;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.schedule.mapper.ScheduleMapper;
import com.example.petapp.domain.schedule.model.dto.request.TimeDto;
import com.example.petapp.domain.schedule.model.dto.response.GetSchedulesResponseDto;
import com.example.petapp.domain.walkingtogetherPost.WalkingTogetherPostRepository;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService implements ScheduleUseCase {

    private final ProfileUseCase profileUseCase;
    private final MemberRepository memberRepository;
    private final WalkingTogetherPostRepository walkingTogetherPostRepository;
    private final DelegateWalkPostRepository delegateWalkPostRepository;

    @NotNull
    private static TimeDto getTime(String start, String end) {
        LocalDateTime startDateTime = LocalDate.parse(start).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(end).atTime(23, 59);
        return new TimeDto(startDateTime, endDateTime);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetSchedulesResponseDto> getSchedules(String start, String end, Long profileId) {

        List<GetSchedulesResponseDto> getSchedulesResponseDtos = new ArrayList<>();
        TimeDto timeDto = getTime(start, end);
        Optional<Profile> profile = profileUseCase.find(profileId);

        if (profile.isPresent()) {
            List<WalkingTogetherPost> walkingTogetherPostList =
                    walkingTogetherPostRepository.findAllByProfileContainsAndScheduledTimeBetween(profile.get(), timeDto.getStart(), timeDto.getEnd());
            List<GetSchedulesResponseDto> list = walkingTogetherPostList.stream()
                    .map(walkingTogetherPost -> ScheduleMapper.toGetSchedulesResponseDto(profile.get(), walkingTogetherPost)
                    ).toList();
            getSchedulesResponseDtos.addAll(list);

        }

        Member member = memberRepository.find(profile.get().getMember().getId()).get();
        List<DelegateWalkPost> delegateWalkPostList =
                delegateWalkPostRepository.findList(member.getId(), timeDto.getStart(), timeDto.getEnd());

        List<GetSchedulesResponseDto> list = delegateWalkPostList.stream()
                .map(delegateWalkPost -> ScheduleMapper.toGetSchedulesResponseDto(member, delegateWalkPost)
                ).toList();
        getSchedulesResponseDtos.addAll(list);

        return getSchedulesResponseDtos;
    }

}
