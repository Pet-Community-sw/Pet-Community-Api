package com.example.petapp.infrastructure.database.jpa.member;

import com.example.petapp.domain.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaRoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
}
