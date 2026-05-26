package com.example.petapp.application.usecase.petbreed.service;

import com.example.petapp.application.usecase.petbreed.PetBreedUseCase;
import com.example.petapp.domain.petbreed.PetBreedRepository;
import com.example.petapp.domain.petbreed.model.PetBreed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PetBreedService implements PetBreedUseCase {

    private final PetBreedRepository repository;

    @Override
    public void save(PetBreed petBreed) {
        repository.save(petBreed);
    }
}
