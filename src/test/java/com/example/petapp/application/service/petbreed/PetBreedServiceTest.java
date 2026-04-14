package com.example.petapp.application.service.petbreed;

import com.example.petapp.domain.petbreed.PetBreedRepository;
import com.example.petapp.domain.petbreed.model.PetBreed;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PetBreedServiceTest {

    @Mock
    private PetBreedRepository repository;

    @InjectMocks
    private PetBreedService petBreedService;

    @Test
    void 종_저장은_저장소에_위임한다() {
        PetBreed petBreed = PetBreed.builder().name("Poodle").build();

        petBreedService.save(petBreed);

        verify(repository).save(petBreed);
    }
}
