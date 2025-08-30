package com.example.PetApp.domain.petbreed;

import com.example.PetApp.domain.petbreed.model.entity.PetBreed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PetBreedServiceImpl implements PetBreedService {

    private final PetBreedRepository petBreedRepository;
    @Override
    public Optional<PetBreed> findByName(String name) {
        return petBreedRepository.findByName(name);
    }

    public Optional<PetBreed> findById(Long id) {
        return petBreedRepository.findById(id);
    }


}
