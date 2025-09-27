package com.example.PetApp.domain.fcm.model.entity;

import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.infrastructure.database.base.superclass.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Getter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmToken extends BaseEntity {


    @Setter
    @NotBlank
    @Column(nullable = false)
    private String fcmToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
