package com.example.petapp.application.service.petbreed;

import com.example.petapp.application.in.petbreed.PetBreedQueryUseCase;
import com.example.petapp.application.in.petbreed.dto.PetBreedGetListDto;
import com.example.petapp.common.exception.NotFoundException;
import com.example.petapp.domain.petbreed.PetBreedRepository;
import com.example.petapp.domain.petbreed.model.PetBreed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class PetBreedQueryService implements PetBreedQueryUseCase {

    private final PetBreedRepository repository;

    @Override
    public Long count() {
        return repository.count();
    }

    @Override
    public PetBreed findOrThrow(Long id) {
        return repository.find(id).orElseThrow(() -> new NotFoundException("해당 종은 없습니다."));
    }

    @Override
    public PetBreed findOrThrow(String name) {
        return repository.find(name).orElseThrow(() -> new NotFoundException("해당 종은 없습니다."));
    }

    @Override
    public List<PetBreedGetListDto> getList() {
        return repository.findAll();
    }
}
