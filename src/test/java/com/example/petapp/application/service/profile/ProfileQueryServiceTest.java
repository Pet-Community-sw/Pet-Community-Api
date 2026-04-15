package com.example.petapp.application.service.profile;

import com.example.petapp.domain.profile.ProfileRepository;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.interfaces.exception.ForbiddenException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileQueryServiceTest {

    @Mock
    private ProfileRepository repository;

    @InjectMocks
    private ProfileQueryService profileQueryService;

    @Test
    void 프로필ID목록으로_조회하면_ID를_키로_하는_맵을_반환한다() {
        Profile profile1 = org.mockito.Mockito.mock(Profile.class);
        Profile profile2 = org.mockito.Mockito.mock(Profile.class);
        when(repository.findAllByIds(Set.of(1L, 2L))).thenReturn(List.of(profile1, profile2));
        when(profile1.getId()).thenReturn(1L);
        when(profile2.getId()).thenReturn(2L);

        Map<Long, Profile> result = profileQueryService.findMapOrThrow(Set.of(1L, 2L));

        assertThat(result).containsEntry(1L, profile1).containsEntry(2L, profile2);
        verify(repository).findAllByIds(Set.of(1L, 2L));
    }

    @Test
    void 요청한_프로필이_누락되면_예외가_발생한다() {
        Profile profile1 = org.mockito.Mockito.mock(Profile.class);
        when(repository.findAllByIds(Set.of(1L, 2L))).thenReturn(List.of(profile1));
        when(profile1.getId()).thenReturn(1L);

        assertThatThrownBy(() -> profileQueryService.findMapOrThrow(Set.of(1L, 2L)))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("프로필을 등록해주세요.");
    }

    @Test
    void 빈_ID목록이면_빈_맵을_반환한다() {
        Map<Long, Profile> result = profileQueryService.findMapOrThrow(Set.of());

        assertThat(result).isEmpty();
    }

    @Test
    void 프로필이_존재하면_findOrThrow_조회에_성공한다() {
        Profile profile = org.mockito.Mockito.mock(Profile.class);
        when(repository.find(1L)).thenReturn(Optional.of(profile));

        Profile result = profileQueryService.findOrThrow(1L);

        assertThat(result).isSameAs(profile);
    }
}
