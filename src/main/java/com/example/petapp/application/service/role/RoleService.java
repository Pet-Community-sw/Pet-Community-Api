package com.example.petapp.application.service.role;

import com.example.petapp.application.in.role.RoleUseCase;
import com.example.petapp.domain.role.Role;
import com.example.petapp.domain.role.RoleRepository;
import com.example.petapp.interfaces.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleService implements RoleUseCase {

    private final RoleRepository repository;

    @Transactional(readOnly = true)
    @Override
    public Role findTemporaryRole() {
        return repository.find("ROLE_TEMPORARY").orElseThrow(() -> new NotFoundException("해당 role은 없습니다."));
    }

    @Transactional(readOnly = true)
    @Override
    public Role findUserRole() {
        return repository.find("ROLE_USER").orElseThrow(() -> new NotFoundException("해당 role은 없습니다."));
    }
}
