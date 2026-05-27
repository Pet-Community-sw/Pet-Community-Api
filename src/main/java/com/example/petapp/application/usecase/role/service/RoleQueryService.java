package com.example.petapp.application.usecase.role.service;

import com.example.petapp.application.common.exception.ErrorCode;
import com.example.petapp.application.common.exception.PetCommunityException;
import com.example.petapp.application.usecase.role.RoleQueryUseCase;
import com.example.petapp.domain.role.Role;
import com.example.petapp.domain.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class RoleQueryService implements RoleQueryUseCase {

    private final RoleRepository repository;

    @Override
    public Role findTemporaryRole() {
        return repository.find("ROLE_TEMPORARY").orElseThrow(() -> new PetCommunityException(ErrorCode.NOT_FOUND, "해당 role은 없습니다."));
    }

    @Override
    public Role findUserRole() {
        return repository.find("ROLE_USER").orElseThrow(() -> new PetCommunityException(ErrorCode.NOT_FOUND, "해당 role은 없습니다."));
    }

}
