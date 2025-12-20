package com.example.petapp.application.service.role;

import com.example.petapp.application.in.role.RoleQueryUseCase;
import com.example.petapp.domain.role.Role;
import com.example.petapp.domain.role.RoleRepository;
import com.example.petapp.interfaces.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class RoleQueryService implements RoleQueryUseCase {

    private final RoleRepository repository;

    @Override
    public Role findOrThrow() {
        return repository.find("ROLE_USER").orElseThrow(() -> new NotFoundException("해당 role은 없습니다."));
    }

}
