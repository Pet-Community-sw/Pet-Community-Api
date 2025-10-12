package com.example.petapp.domain.schedule;

import com.example.petapp.domain.post.delegate.model.entity.DelegateWalkPost;
import com.example.petapp.domain.member.model.entity.Member;
import com.example.petapp.domain.profile.model.entity.Profile;
import com.example.petapp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import com.example.petapp.domain.schedule.model.dto.response.GetSchedulesResponseDto;
import com.example.petapp.domain.schedule.model.dto.request.TimeDto;
import com.example.petapp.domain.schedule.mapper.ScheduleMapper;
import com.example.petapp.domain.post.delegate.DelegateWalkPostRepository;
import com.example.petapp.domain.member.MemberRepository;
import com.example.petapp.domain.profile.ProfileRepository;
import com.example.petapp.domain.walkingtogethermatch.WalkingTogetherMatchRepository;
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
public class ScheduleServiceImpl implements ScheduleService {

    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final WalkingTogetherMatchRepository walkingTogetherMatchRepository;
    private final DelegateWalkPostRepository delegateWalkPostRepository;

    @Transactional(readOnly = true)
    @Override
    public List<GetSchedulesResponseDto> getSchedules(String start, String end, Long profileId) {

        List<GetSchedulesResponseDto> getSchedulesResponseDtos = new ArrayList<>();
        TimeDto timeDto = getTime(start, end);
        Optional<Profile> profile = profileRepository.findById(profileId);

        if (profile.isPresent()) {
            List<WalkingTogetherMatch> walkingTogetherMatchList =
                    walkingTogetherMatchRepository.findAllByProfileContainsAndScheduledTimeBetween(profile.get(), timeDto.getStart(), timeDto.getEnd());
            List<GetSchedulesResponseDto> list = walkingTogetherMatchList.stream()
                    .map(walkingTogetherPost -> ScheduleMapper.toGetSchedulesResponseDto(profile.get(), walkingTogetherPost)
            ).toList();
            getSchedulesResponseDtos.addAll(list);

        }

        Member member = memberRepository.findById(profile.get().getMember().getId()).get();
        List<DelegateWalkPost> delegateWalkPostList =
                delegateWalkPostRepository.findAllBySelectedApplicantMemberIdAndScheduledTimeBetween(member.getId(), timeDto.getStart(), timeDto.getEnd());

        List<GetSchedulesResponseDto> list = delegateWalkPostList.stream()
                .map(delegateWalkPost -> ScheduleMapper.toGetSchedulesResponseDto(member, delegateWalkPost)
                ).toList();
        getSchedulesResponseDtos.addAll(list);

        return getSchedulesResponseDtos;
    }

    @NotNull
    private static TimeDto getTime(String start, String end) {
        LocalDateTime startDateTime = LocalDate.parse(start).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(end).atTime(23, 59);
        return new TimeDto(startDateTime, endDateTime);
    }

}
