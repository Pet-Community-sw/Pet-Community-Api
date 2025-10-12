package com.example.petapp.domain.petbreed.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PetBreedGetListDto {

    private Long petBreedId;

    private String petBreedName;
}
