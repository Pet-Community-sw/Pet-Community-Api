package com.example.PetApp.domain.token.model.entity;


import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.infrastructure.database.base.superclass.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class RefreshToken extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", unique = true)
    private Member member;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String refreshToken;

}
