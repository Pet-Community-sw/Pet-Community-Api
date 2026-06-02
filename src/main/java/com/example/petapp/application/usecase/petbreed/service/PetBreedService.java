package com.example.petapp.application.usecase.petbreed.service;

import com.example.petapp.application.common.exception.ErrorCode;
import com.example.petapp.application.common.exception.PetCommunityException;
import com.example.petapp.application.usecase.petbreed.PetBreedUseCase;
import com.example.petapp.application.usecase.petbreed.dto.PetBreedGetListDto;
import com.example.petapp.domain.petbreed.PetBreedRepository;
import com.example.petapp.domain.petbreed.model.PetBreed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetBreedService implements PetBreedUseCase {

    private final PetBreedRepository repository;

    @Override
    public void save(PetBreed petBreed) {
        repository.save(petBreed);
    }

    @Transactional(readOnly = true)
    @Override
    public Long count() {
        return repository.count();
    }

    @Transactional(readOnly = true)
    @Override
    public PetBreed findOrThrow(Long id) {
        return repository.find(id).orElseThrow(() -> new PetCommunityException(ErrorCode.NOT_FOUND, "해당 종은 없습니다."));
    }

    @Transactional(readOnly = true)
    @Override
    public PetBreed findOrThrow(String name) {
        return repository.find(name).orElseThrow(() -> new PetCommunityException(ErrorCode.NOT_FOUND, "해당 종은 없습니다."));
    }

    @Transactional(readOnly = true)
    @Override
    public List<PetBreedGetListDto> getList() {
        return repository.findAll();
    }
}
