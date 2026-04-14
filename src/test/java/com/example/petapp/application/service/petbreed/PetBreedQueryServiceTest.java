package com.example.petapp.application.service.petbreed;

import com.example.petapp.application.in.petbreed.dto.PetBreedGetListDto;
import com.example.petapp.domain.petbreed.PetBreedRepository;
import com.example.petapp.domain.petbreed.model.PetBreed;
import com.example.petapp.interfaces.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetBreedQueryServiceTest {

    @Mock
    private PetBreedRepository repository;

    @InjectMocks
    private PetBreedQueryService petBreedQueryService;

    @Test
    void 종_개수_조회는_저장소_결과를_반환한다() {
        when(repository.count()).thenReturn(12L);

        Long result = petBreedQueryService.count();

        assertThat(result).isEqualTo(12L);
        verify(repository).count();
    }

    @Test
    void ID로_종이_존재하면_조회에_성공한다() {
        PetBreed petBreed = PetBreed.builder().name("Poodle").build();
        when(repository.find(1L)).thenReturn(Optional.of(petBreed));

        PetBreed result = petBreedQueryService.findOrThrow(1L);

        assertThat(result).isSameAs(petBreed);
        verify(repository).find(1L);
    }

    @Test
    void ID로_종이_없으면_예외가_발생한다() {
        when(repository.find(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> petBreedQueryService.findOrThrow(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 종은 없습니다.");
    }

    @Test
    void 이름으로_종이_존재하면_조회에_성공한다() {
        PetBreed petBreed = PetBreed.builder().name("Poodle").build();
        when(repository.find("Poodle")).thenReturn(Optional.of(petBreed));

        PetBreed result = petBreedQueryService.findOrThrow("Poodle");

        assertThat(result).isSameAs(petBreed);
        verify(repository).find("Poodle");
    }

    @Test
    void 이름으로_종이_없으면_예외가_발생한다() {
        when(repository.find("Poodle")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> petBreedQueryService.findOrThrow("Poodle"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 종은 없습니다.");
    }

    @Test
    void 종_목록_조회는_저장소_목록을_그대로_반환한다() {
        List<PetBreedGetListDto> expected = List.of(
                new PetBreedGetListDto(1L, "Poodle"),
                new PetBreedGetListDto(2L, "Maltese")
        );
        when(repository.findAll()).thenReturn(expected);

        List<PetBreedGetListDto> result = petBreedQueryService.getList();

        assertThat(result).isEqualTo(expected);
        verify(repository).findAll();
    }
}
