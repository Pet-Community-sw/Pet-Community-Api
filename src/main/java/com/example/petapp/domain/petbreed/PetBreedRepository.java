package com.example.petapp.domain.petbreed;

import com.example.petapp.application.in.petbreed.dto.PetBreedGetListDto;
import com.example.petapp.domain.petbreed.model.PetBreed;

import java.util.List;
import java.util.Optional;

public interface PetBreedRepository {

    Optional<PetBreed> find(String name);

    Optional<PetBreed> find(Long id);

    List<PetBreedGetListDto> findAll();

    Long count();

    void save(PetBreed petBreed);
}
