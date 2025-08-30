package com.example.PetApp.domain.petbreed;

import com.example.PetApp.domain.petbreed.model.entity.PetBreed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetBreedRepository extends JpaRepository<PetBreed, Long> {

    Optional<PetBreed> findByName(String name);
}
