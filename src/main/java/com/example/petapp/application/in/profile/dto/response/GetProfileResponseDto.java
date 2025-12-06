package com.example.petapp.application.in.profile.dto.response;

import com.example.petapp.domain.petbreed.model.entity.PetBreed;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetProfileResponseDto {
    private Long profileId;

    private String petBreedName;

    private String petImageUrl;

    private Long memberId;

    private String petName;

    private LocalDate petBirthDate;

    private String petAge;

    private Set<PetBreed> avoidBreeds;

    private boolean isOwner;
}
