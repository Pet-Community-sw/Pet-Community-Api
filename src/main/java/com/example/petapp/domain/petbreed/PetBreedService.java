package com.example.petapp.domain.petbreed;

import com.example.petapp.domain.petbreed.model.dto.PetBreedGetListDto;
import com.example.petapp.domain.petbreed.model.entity.PetBreed;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface PetBreedService {

    Optional<PetBreed> findById(Long id);

    List<PetBreedGetListDto> getList();

}
