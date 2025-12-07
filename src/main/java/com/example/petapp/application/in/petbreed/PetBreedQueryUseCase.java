package com.example.petapp.application.in.petbreed;

import com.example.petapp.application.in.petbreed.dto.PetBreedGetListDto;
import com.example.petapp.domain.petbreed.model.PetBreed;

import java.util.List;

public interface PetBreedQueryUseCase {

    Long count();

    PetBreed find(Long id);

    PetBreed find(String name);

    List<PetBreedGetListDto> getList();

}
