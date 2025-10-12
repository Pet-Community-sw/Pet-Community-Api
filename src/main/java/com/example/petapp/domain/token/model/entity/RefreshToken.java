package com.example.petapp.domain.token.model.entity;


import com.example.petapp.common.exception.ForbiddenException;
import com.example.petapp.domain.member.model.entity.Member;
import com.example.petapp.common.base.superclass.BaseEntity;
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
    @Column(nullable = false, length = 512)
    private String refreshToken;

    public void isEqual(String refreshToken) {
        if (!this.refreshToken.equals(refreshToken)) {
            throw new ForbiddenException("RefreshToken이 유효하지 않습니다.");
        }
    }
}
