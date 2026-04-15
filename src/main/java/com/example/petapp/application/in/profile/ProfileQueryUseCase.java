package com.example.petapp.application.in.profile;

import com.example.petapp.domain.profile.model.Profile;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ProfileQueryUseCase {
    Profile findOrThrow(Long id);

    Optional<Profile> find(Long id);

    Map<Long, Profile> findMapOrThrow(Set<Long> ids);
}
