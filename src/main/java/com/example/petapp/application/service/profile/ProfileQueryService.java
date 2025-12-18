package com.example.petapp.application.service.profile;

import com.example.petapp.application.in.profile.ProfileQueryUseCase;
import com.example.petapp.domain.profile.ProfileRepository;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.interfaces.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ProfileQueryService implements ProfileQueryUseCase {

    private final ProfileRepository repository;

    @Override
    public Profile findOrThrow(Long id) {
        return repository.find(id).orElseThrow(() -> new ForbiddenException("프로필을 등록해주세요."));
    }

    @Override
    public Optional<Profile> find(Long id) {
        return repository.find(id);
    }
}
