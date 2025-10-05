package com.example.PetApp.domain.petbreed;

import com.example.PetApp.domain.petbreed.model.dto.PetBreedGetListDto;
import com.example.PetApp.domain.petbreed.model.entity.PetBreed;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface PetBreedService {

    Optional<PetBreed> findById(Long id);

    List<PetBreedGetListDto> getList();

}
