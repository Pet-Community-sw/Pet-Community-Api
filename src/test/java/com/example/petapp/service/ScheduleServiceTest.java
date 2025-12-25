//package com.example.petapp.service;
//
//
//import com.example.petapp.domain.member.MemberRepository;
//import com.example.petapp.domain.member.model.Member;
//import com.example.petapp.domain.post.DelegateWalkPostRepository;
//import com.example.petapp.domain.post.model.DelegateWalkPost;
//import com.example.petapp.domain.profile.ProfileRepository;
//import com.example.petapp.domain.profile.model.Profile;
//import com.example.petapp.domain.schedule.ScheduleServiceImpl;
//import com.example.petapp.domain.schedule.model.dto.response.GetSchedulesResponseDto;
//import com.example.petapp.domain.schedule.model.dto.response.ScheduleType;
//import com.example.petapp.domain.walkingtogethermatch.WalkingTogetherMatchRepository;
//import com.example.petapp.domain.walkingtogethermatch.model.WalkingTogetherMatch;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class ScheduleServiceTest {
//
//    @InjectMocks
//    private ScheduleServiceImpl scheduleServiceImpl;
//    @Mock
//    private ProfileRepository profileRepository;
//    @Mock
//    private MemberRepository memberRepository;
//    @Mock
//    private WalkingTogetherMatchRepository walkingTogetherMatchRepository;
//    @Mock
//    private DelegateWalkPostRepository delegateWalkPostRepository;
//
//    @Test
//    @DisplayName("getSchedules_성공")
//    void test1() {
//        // Given
//        Long profileId = 1L;
//        String start = "2025-06-01";
//        String end = "2025-06-30";
//
//        LocalDateTime startDateTime = LocalDateTime.of(2025, 6, 1, 0, 0);
//        LocalDateTime endDateTime = LocalDateTime.of(2025, 6, 30, 23, 59);
//
//        Member member = Member.builder().memberId(10L).email("test").build();
//        Profile profile = Profile.builder().profileId(profileId).member(member).build();
//
//        WalkingTogetherMatch walkPost = WalkingTogetherMatch.builder()
//                .scheduledTime(LocalDateTime.of(2025, 6, 10, 18, 0))
//                .build();
//
//        DelegateWalkPost delegatePost = DelegateWalkPost.builder()
//                .scheduledTime(LocalDateTime.of(2025, 6, 12, 10, 0))
//                .build();
//
//        // Mocking
//        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
//        when(walkingTogetherMatchRepository.findAllByProfileContainsAndScheduledTimeBetween(profile, startDateTime, endDateTime))
//                .thenReturn(List.of(walkPost));
//        when(memberRepository.find(member.getMemberId())).thenReturn(Optional.of(member));
//        when(delegateWalkPostRepository.findList(member.getMemberId(), startDateTime, endDateTime))
//                .thenReturn(List.of(delegatePost));
//
//        // When
//        List<GetSchedulesResponseDto> result = scheduleServiceImpl.getSchedules(start, end, profileId);
//
//        // Then
//        assertThat(result).hasSize(2);
//
//        assertThat(result).anySatisfy(dto -> {
//            assertThat(dto.getScheduleType()).isEqualTo(ScheduleType.WALKING_TOGETHER);
//            assertThat(dto.getScheduleDate()).isEqualTo(walkPost.getScheduledTime());
//        });
//
//        assertThat(result).anySatisfy(dto -> {
//            assertThat(dto.getScheduleType()).isEqualTo(ScheduleType.DELEGATE_WALK);
//            assertThat(dto.getScheduleDate()).isEqualTo(delegatePost.getScheduledTime());
//        });
//
//        // Verify
//        verify(profileRepository).findById(profileId);
//        verify(walkingTogetherMatchRepository).findAllByProfileContainsAndScheduledTimeBetween(profile, startDateTime, endDateTime);
//        verify(memberRepository).find(member.getMemberId());
//        verify(delegateWalkPostRepository).findList(member.getMemberId(), startDateTime, endDateTime);
//    }
//
//}
