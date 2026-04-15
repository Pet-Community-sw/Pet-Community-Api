package com.example.petapp.application.service.profile;

import com.example.petapp.application.in.profile.ProfileQueryUseCase;
import com.example.petapp.domain.profile.ProfileRepository;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.interfaces.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Override
    public Map<Long, Profile> findMapOrThrow(Set<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }

        Map<Long, Profile> profileMap = repository.findAllByIds(ids).stream()
                .collect(Collectors.toMap(Profile::getId, Function.identity()));

        if (profileMap.size() != ids.size()) {
            throw new ForbiddenException("프로필을 등록해주세요.");
        }

        return profileMap;
    }
}
