package com.example.petapp.domain.member;

import com.example.petapp.domain.member.model.Role;

import java.util.Optional;

public interface RoleRepository {

    Optional<Role> find(String name);

    Long count();

    void save(Role role);
}
