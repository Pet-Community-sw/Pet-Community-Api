package com.example.petapp.application.in.role;

import com.example.petapp.domain.role.Role;

public interface RoleQueryUseCase {
    Role findOrThrow();
}
