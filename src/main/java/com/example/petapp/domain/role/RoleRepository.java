package com.example.petapp.domain.role;

import java.util.Optional;

public interface RoleRepository {

    Optional<Role> find(String name);

    Long count();

    void save(Role role);
}
