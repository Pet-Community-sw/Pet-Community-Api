package com.example.petapp.domain.petbreed.model;

import com.example.petapp.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@SuperBuilder
public class PetBreed extends BaseEntity {

    @NotBlank
    @Column(nullable = false, updatable = false)
    private String name;

}
