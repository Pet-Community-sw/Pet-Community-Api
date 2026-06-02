package com.example.petapp.application.usecase.role;

import com.example.petapp.domain.role.Role;

public interface RoleUseCase {
    Role findTemporaryRole();

    Role findUserRole();
}
