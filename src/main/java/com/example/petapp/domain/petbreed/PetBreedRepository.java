package com.example.petapp.domain.petbreed;

import com.example.petapp.domain.petbreed.model.dto.PetBreedGetListDto;
import com.example.petapp.domain.petbreed.model.entity.PetBreed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetBreedRepository extends JpaRepository<PetBreed, Long> {

    Optional<PetBreed> findByName(String name);

    @Query("select new com.example.petapp.domain.petbreed.model.dto.PetBreedGetListDto(p.id, p.name) " +
            "from PetBreed p")
    List<PetBreedGetListDto> findAllPetBreed();

}
