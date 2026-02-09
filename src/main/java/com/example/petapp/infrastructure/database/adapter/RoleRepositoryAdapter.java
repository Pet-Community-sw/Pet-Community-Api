package com.example.petapp.infrastructure.database.adapter;

import com.example.petapp.domain.role.Role;
import com.example.petapp.domain.role.RoleRepository;
import com.example.petapp.infrastructure.database.jpa.member.JpaRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepository {

    private final JpaRoleRepository repository;

    @Override
    public Optional<Role> find(String name) {
        return repository.findByName(name);
    }

    @Override
    public Long count() {
        return repository.count();
    }

    @Override
    public void save(Role role) {
        repository.save(role);
    }
}
