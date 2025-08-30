package com.example.PetApp.domain.petbreed;

import com.example.PetApp.domain.petbreed.model.entity.PetBreed;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface PetBreedService {

    Optional<PetBreed> findByName(String name);

    Optional<PetBreed> findById(Long id);

}
