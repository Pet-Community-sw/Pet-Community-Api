package com.example.petapp.application.in.profile;

import com.example.petapp.domain.profile.model.Profile;

import java.util.Optional;

public interface ProfileQueryUseCase {
    Profile findOrThrow(Long id);

    Optional<Profile> find(Long id);
}
