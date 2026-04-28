package com.example.petapp.application.in.role;

import com.example.petapp.domain.role.Role;

public interface RoleUseCase {
    Role findTemporaryRole();

    Role findUserRole();
}
