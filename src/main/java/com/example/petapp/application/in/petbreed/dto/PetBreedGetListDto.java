package com.example.petapp.application.in.petbreed.dto;

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
