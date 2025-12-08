package com.example.petapp.service;

import com.example.petapp.application.in.chatroom.ChatRoomUseCase;
import com.example.petapp.application.in.chatroom.dto.response.CreateChatRoomResponseDto;
import com.example.petapp.application.in.match.dto.request.CreateWalkingTogetherMatchDto;
import com.example.petapp.application.in.match.dto.response.CreateWalkingTogetherMatchResponseDto;
import com.example.petapp.application.in.match.dto.response.GetWalkingTogetherMatchResponseDto;
import com.example.petapp.application.service.match.WalkingTogetherMatchService;
import com.example.petapp.common.base.embedded.Content;
import com.example.petapp.common.exception.ConflictException;
import com.example.petapp.common.exception.ForbiddenException;
import com.example.petapp.common.exception.NotFoundException;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.petbreed.PetBreedRepository;
import com.example.petapp.domain.petbreed.model.PetBreed;
import com.example.petapp.domain.post.RecommendRoutePostRepository;
import com.example.petapp.domain.post.model.RecommendRoutePost;
import com.example.petapp.domain.profile.ProfileRepository;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.walkingtogethermatch.WalkingTogetherMatchRepository;
import com.example.petapp.domain.walkingtogethermatch.model.WalkingTogetherMatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WalkingTogetherMatchUseCaseTest {
    @InjectMocks
    private WalkingTogetherMatchService walkingTogetherPostServiceImpl;
    @Mock
    private ChatRoomUseCase chatRoomUseCase;
    @Mock
    private WalkingTogetherMatchRepository walkingTogetherMatchRepository;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private PetBreedRepository petBreedRepository;
    @Mock
    private RecommendRoutePostRepository recommendRoutePostRepository;

    private PetBreed petBreed;

    private Profile profile;

    @BeforeEach
    void setUp() {
        petBreed = PetBreed.builder()
                .petBreedId(1L)
                .name("푸들")
                .build();

        profile = Profile.builder()
                .profileId(1L)
                .petBreed(petBreed)
                .avoidBreeds(Set.of(petBreed))
                .build();

    }

    @Test
    @DisplayName("getWalkingTogetherPost_성공")
    void test1() {
        //given
        Long walkingTogetherPostId = 1L;
        Long profileId = 1L;
        PetBreed petBreed = PetBreed.builder()
                .petBreedId(1L)
                .name("리")
                .build();

        Profile profile = Profile.builder()
                .profileId(1L)
                .petBreed(petBreed)
                .avoidBreeds(Set.of(petBreed))
                .build();

        WalkingTogetherMatch walkingTogetherMatch = WalkingTogetherMatch.builder()
                .walkingTogetherPostId(1L)
                .profile(profile)
                .scheduledTime(LocalDateTime.now())
                .profiles(Set.of(2L))
                .limitCount(2)
                .avoidBreeds(Set.of(1L))
                .build();

        ReflectionTestUtils.setField(walkingTogetherMatch, "createdAt", LocalDateTime.now());

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(walkingTogetherMatchRepository.findById(walkingTogetherPostId)).thenReturn(Optional.of(walkingTogetherMatch));
        when(petBreedRepository.find(any())).thenReturn(Optional.of(petBreed));


        //when
        GetWalkingTogetherMatchResponseDto result = walkingTogetherPostServiceImpl.getWalkingTogetherPost(walkingTogetherPostId, profileId);

        //then
        assertThat(result.getWalkingTogetherPostId()).isEqualTo(1L);
        assertThat(result.isOwner()).isTrue();
        assertThat(result.isFiltering()).isTrue();
    }

    @Test
    @DisplayName("getWalkingTogetherPost_프로필이 존재하지 않는 경우_실패")
    void test2() {
        // given
        Long walkingTogetherPostId = 1L;
        Long profileId = 10L;

        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> walkingTogetherPostServiceImpl.getWalkingTogetherPost(walkingTogetherPostId, profileId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("프로필 설정 해주세요.");

    }

    @Test
    @DisplayName("getWalkingTogetherPost_함께 산책해요 게시글이 없는 경우_실패")
    void test3() {
        // given
        Long walkingTogetherPostId = 1L;
        Long profileId = 10L;

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(walkingTogetherMatchRepository.findById(walkingTogetherPostId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> walkingTogetherPostServiceImpl.getWalkingTogetherPost(walkingTogetherPostId, profileId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 함께 산책해요 게시글은 없습니다.");
    }

    @Test
    @DisplayName("getWalkingTogetherPosts_성공")
    void test4() {
        // given
        Long profileId = 1L;
        Long recommendRoutePostId = 2L;

        RecommendRoutePost recommendRoutePost = RecommendRoutePost.builder()
                .postId(recommendRoutePostId)
                .member(Member.builder().memberId(1L).build())
                .content(new Content("산책길추천 1", "좋은 산책길"))
                .build();

        PetBreed petBreed = PetBreed.builder()
                .petBreedId(100L)
                .name("푸들")
                .build();

        WalkingTogetherMatch post1 = WalkingTogetherMatch.builder()
                .walkingTogetherPostId(101L)
                .profile(profile)
                .recommendRoutePost(recommendRoutePost)
                .scheduledTime(LocalDateTime.now())
                .build();
        ReflectionTestUtils.setField(post1, "createdAt", LocalDateTime.now());

        WalkingTogetherMatch post2 = WalkingTogetherMatch.builder()
                .walkingTogetherPostId(102L)
                .profile(profile)
                .recommendRoutePost(recommendRoutePost)
                .scheduledTime(LocalDateTime.now())
                .build();
        ReflectionTestUtils.setField(post2, "createdAt", LocalDateTime.now());

        List<WalkingTogetherMatch> postList = List.of(post1, post2);

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(recommendRoutePostRepository.findById(recommendRoutePostId)).thenReturn(Optional.of(recommendRoutePost));
        when(petBreedRepository.find("푸들")).thenReturn(Optional.of(petBreed));
        when(walkingTogetherMatchRepository.findAllByRecommendRoutePost(recommendRoutePost)).thenReturn(postList);

        // when
        List<GetWalkingTogetherMatchResponseDto> result =
                walkingTogetherPostServiceImpl.getWalkingTogetherPosts(recommendRoutePostId, profileId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getWalkingTogetherPostId()).isEqualTo(101L);
        assertThat(result.get(1).getWalkingTogetherPostId()).isEqualTo(102L);

        verify(profileRepository).findById(profileId);
        verify(recommendRoutePostRepository).findById(recommendRoutePostId);
        verify(petBreedRepository).find("푸들");
        verify(walkingTogetherMatchRepository).findAllByRecommendRoutePost(recommendRoutePost);
    }

    @Test
    @DisplayName("getWalkingTogetherPosts_프로필이 없는 경우_실패")
    void test5() {
        //given
        Long profileId = 1L;
        Long recommendRoutePostId = 2L;

        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> walkingTogetherPostServiceImpl.getWalkingTogetherPosts(recommendRoutePostId, profileId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("프로필 설정 해주세요.");
    }

    @Test
    @DisplayName("getWalkingTogetherPosts_산책길 추천 게시글이 없는 경우_실패")
    void test6() {
        Long profileId = 1L;
        Long recommendRoutePostId = 2L;

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(recommendRoutePostRepository.findById(recommendRoutePostId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> walkingTogetherPostServiceImpl.getWalkingTogetherPosts(recommendRoutePostId, profileId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 산책길 추천 게시글은 없습니다.");
    }

    @Test
    @DisplayName("createWalkingTogetherPost_성공")
    void test7() {
        // given
        Long profileId = 1L;
        Long recommendRoutePostId = 2L;

        CreateWalkingTogetherMatchDto dto = CreateWalkingTogetherMatchDto.builder()
                .recommendRoutePostId(recommendRoutePostId)
                .scheduledTime(LocalDateTime.now().plusDays(1))
                .limitCount(3)
                .build();

        RecommendRoutePost recommendRoutePost = RecommendRoutePost.builder()
                .postId(recommendRoutePostId)
                .content(new Content("산책길추천 1", "좋은 산책길"))
                .build();

        when(recommendRoutePostRepository.findById(recommendRoutePostId)).thenReturn(Optional.of(recommendRoutePost));
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(walkingTogetherMatchRepository.save(any(WalkingTogetherMatch.class))).thenReturn(WalkingTogetherMatch.builder().walkingTogetherPostId(100L).build());
//        when(walkingTogetherPostRepository.save(any(WalkingTogetherMatch.class))).thenAnswer(invocation -> {
//            WalkingTogetherMatch walkingTogetherMatch = invocation.getArgument(0);
//            return walkingTogetherMatch.toBuilder().walkingTogetherPostId(100L).build();
//        });

        // when
        CreateWalkingTogetherMatchResponseDto result =
                walkingTogetherPostServiceImpl.createWalkingTogetherPost(dto, profileId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getWalkingTogetherPostId()).isEqualTo(100L);

        verify(recommendRoutePostRepository).findById(recommendRoutePostId);
        verify(profileRepository).findById(profileId);
        verify(walkingTogetherMatchRepository).save(any(WalkingTogetherMatch.class));
    }

    @Test
    @DisplayName("createWalkingTogetherPost_프로필이 없는 경우_실패")
    void test8() {
        //given
        Long profileId = 1L;

        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> walkingTogetherPostServiceImpl.createWalkingTogetherPost(any(CreateWalkingTogetherMatchDto.class), profileId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("프로필 설정 해주세요.");
    }

    @Test
    @DisplayName("createWalkingTogetherPost_산책길 추천 게시글이 없는 경우_실패")
    void test9() {
        // given
        Long profileId = 1L;

        Profile profile = Profile.builder()
                .profileId(1L)
                .build();

        CreateWalkingTogetherMatchDto createWalkingTogetherMatchDto = CreateWalkingTogetherMatchDto.builder()
                .recommendRoutePostId(1L)
                .build();

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(recommendRoutePostRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> walkingTogetherPostServiceImpl.createWalkingTogetherPost(createWalkingTogetherMatchDto, profileId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 산책길 추천 게시글은 없습니다.");

    }

    @Test
    @DisplayName("startMatch_성공")
    void test10() {
        // given
        Long walkingTogetherPostId = 1L;
        Long profileId = 2L;

        WalkingTogetherMatch post = WalkingTogetherMatch.builder()
                .walkingTogetherPostId(walkingTogetherPostId)
                .profiles(new HashSet<>()) // 아직 매칭 안 된 상태
                .avoidBreeds(new HashSet<>()) // 피해야 할 종 없음
                .build();

        PetBreed petBreed = PetBreed.builder()
                .petBreedId(99L)
                .name("리트리버")
                .build();

        CreateChatRoomResponseDto chatRoomResponseDto =
                new CreateChatRoomResponseDto(1L, true);

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(walkingTogetherMatchRepository.findById(walkingTogetherPostId)).thenReturn(Optional.of(post));
        when(petBreedRepository.find("푸들")).thenReturn(Optional.of(petBreed));
        when(chatRoomUseCase.createChatRoom(post, profile)).thenReturn(chatRoomResponseDto);

        // when
        CreateChatRoomResponseDto result = walkingTogetherPostServiceImpl.startMatch(walkingTogetherPostId, profileId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getChatRoomId()).isEqualTo(1L);
        verify(chatRoomUseCase).createChatRoom(post, profile);
    }

    @Test
    @DisplayName("startMatch_프로필 없는 경우_실패")
    void test11() {
        //given
        when(profileRepository.findById(2L)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> walkingTogetherPostServiceImpl.startMatch(anyLong(), 2L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("프로필 설정해주세요.");

    }

    @Test
    @DisplayName("startMatch_함께 산책해요 게시글 없는 경우_실패")
    void test12() {
        //given
        Long profileId = 2L;
        Long walkingTogetherPostId = 1L;


        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(walkingTogetherMatchRepository.findById(walkingTogetherPostId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> walkingTogetherPostServiceImpl.startMatch(walkingTogetherPostId, profileId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 함께 산책해요 게시글은 없습니다.");
    }


    @Test
    @DisplayName("startMatch_이미 채팅방에 들어가있는 경우_실패")
    void test13() {
        //given
        Long profileId = 2L;
        Long walkingTogetherPostId = 1L;
        Set<Long> profiles = new HashSet<>();
        profiles.add(profileId);

        WalkingTogetherMatch post = WalkingTogetherMatch.builder()
                .profiles(profiles)
                .build();

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(walkingTogetherMatchRepository.findById(walkingTogetherPostId)).thenReturn(Optional.of(post));

        //when & then
        assertThatThrownBy(() -> walkingTogetherPostServiceImpl.startMatch(walkingTogetherPostId, profileId))
                .isInstanceOf(ConflictException.class)
                .hasMessage("이미 채팅방에 들어가있습니다.");

    }

    @Test
    @DisplayName("startMatch_견종을 찾을 수 없는 경우_실패")
    void test14() {
        //given
        Long walkingTogetherPostId = 1L;
        Long profileId = 2L;

        WalkingTogetherMatch post = WalkingTogetherMatch.builder()
                .profiles(new HashSet<>())
                .avoidBreeds(new HashSet<>())
                .build();

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(walkingTogetherMatchRepository.findById(walkingTogetherPostId)).thenReturn(Optional.of(post));
        when(petBreedRepository.find("푸들")).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> walkingTogetherPostServiceImpl.startMatch(walkingTogetherPostId, profileId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 견종을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("startMatch_피해야하는 견종일 경우_실패")
    void test15() {
        //when
        Long profileId = 2L;
        Long walkingTogetherPostId = 1L;
        Set<Long> profiles = new HashSet<>();
        profiles.add(3L);

        PetBreed petBreed = PetBreed.builder()
                .petBreedId(3L)
                .name("도베르만")
                .build();

        WalkingTogetherMatch post = WalkingTogetherMatch.builder()
                .profiles(new HashSet<>())
                .avoidBreeds(profiles) // 피해야 할 종에 포함됨
                .build();

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(walkingTogetherMatchRepository.findById(walkingTogetherPostId)).thenReturn(Optional.of(post));
        when(petBreedRepository.find("푸들")).thenReturn(Optional.of(petBreed));

        //when & then
        assertThatThrownBy(() -> walkingTogetherPostServiceImpl.startMatch(walkingTogetherPostId, profileId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("해당 종은 참여할 수 없습니다.");
    }

}