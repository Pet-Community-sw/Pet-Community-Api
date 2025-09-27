package com.example.PetApp.domain.petbreed.model.entity;

import com.example.PetApp.infrastructure.database.base.superclass.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

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
