package com.example.petapp.interfaces.controller;

import com.example.petapp.application.in.petbreed.PetBreedQueryUseCase;
import com.example.petapp.application.in.petbreed.dto.PetBreedGetListDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "PetBreed")
@RestController
@RequestMapping("/pet-breeds")
@RequiredArgsConstructor
public class PetBreedController {

    private final PetBreedQueryUseCase petBreedQueryUseCase;

    @Operation(
            summary = "애완 종 목록 조회"
    )
    @GetMapping()
    public List<PetBreedGetListDto> getList() {
        return petBreedQueryUseCase.getList();
    }
}
