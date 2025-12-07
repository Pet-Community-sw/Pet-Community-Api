package com.example.petapp.domain.petbreed.model;

import com.example.petapp.common.base.superclass.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
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
