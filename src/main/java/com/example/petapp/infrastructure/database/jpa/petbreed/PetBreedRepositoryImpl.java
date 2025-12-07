package com.example.petapp.infrastructure.database.jpa.petbreed;

import com.example.petapp.application.in.petbreed.dto.PetBreedGetListDto;
import com.example.petapp.domain.petbreed.PetBreedRepository;
import com.example.petapp.domain.petbreed.model.PetBreed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PetBreedRepositoryImpl implements PetBreedRepository {

    private final JpaPetBreedRepository repository;

    @Override
    public Optional<PetBreed> find(String name) {
        return repository.findByName(name);
    }

    @Override
    public Optional<PetBreed> find(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<PetBreedGetListDto> findAll() {
        return repository.findAllPetBreed();
    }

    @Override
    public Long count() {
        return repository.count();
    }

    @Override
    public void save(PetBreed petBreed) {
        repository.save(petBreed);
    }
}
