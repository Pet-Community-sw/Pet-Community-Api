package com.example.petapp.application.in.petbreed;

import com.example.petapp.application.in.petbreed.dto.PetBreedGetListDto;
import com.example.petapp.domain.petbreed.model.PetBreed;

import java.util.List;

public interface PetBreedQueryUseCase {

    Long count();

    PetBreed findOrThrow(Long id);

    PetBreed findOrThrow(String name);

    List<PetBreedGetListDto> getList();

}
