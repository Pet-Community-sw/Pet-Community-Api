package com.example.petapp.infrastructure.database.jpa.petbreed;

import com.example.petapp.application.in.petbreed.dto.PetBreedGetListDto;
import com.example.petapp.domain.petbreed.model.PetBreed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JpaPetBreedRepository extends JpaRepository<PetBreed, Long> {
    Optional<PetBreed> findByName(String name);

    @Query("select new com.example.petapp.application.in.petbreed.dto.PetBreedGetListDto(p.id, p.name) " +
            "from PetBreed p")
    List<PetBreedGetListDto> findAllPetBreed();
}
