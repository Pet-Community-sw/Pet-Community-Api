package com.example.petapp.domain.role;


import com.example.petapp.domain.BaseEntity;
import com.example.petapp.domain.member.model.MemberRole;
import lombok.*;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Role extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberRole> memberRoles = new ArrayList<>();
}