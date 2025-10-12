package com.example.petapp.domain.petbreed;

import com.example.petapp.domain.petbreed.model.dto.PetBreedGetListDto;
import com.example.petapp.domain.petbreed.model.entity.PetBreed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PetBreedServiceImpl implements PetBreedService {

    private final PetBreedRepository petBreedRepository;

    @Override
    public Optional<PetBreed> findById(Long id) {
        return petBreedRepository.findById(id);
    }

    @Override
    public List<PetBreedGetListDto> getList() {
        return petBreedRepository.findAllPetBreed();
    }
}
