package com.example.petapp.domain.petbreed.model.entity;

import com.example.petapp.common.base.superclass.BaseEntity;
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
