package com.example.PetApp.domain.member.model.entity;

import com.example.PetApp.domain.post.common.Post;
import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.domain.token.model.entity.RefreshToken;
import com.example.PetApp.domain.fcm.model.entity.FcmToken;
import com.example.PetApp.infrastructure.database.shared.superclass.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)//JPA 내부에서는 접근 가능하고, 외부에서는 new로 빈 객체 생성 못 하게
//기본 생성자를 protected로 두는 게 안전하고 객체지향적이다
@AllArgsConstructor
@SuperBuilder
/*
* 부모 클래스의 필드를 equals/hashCode에 포함하고 싶다면
 @EqualsAndHashCode(callSuper = true) 사용
자식 클래스의 필드만 비교하고 싶다면
 @EqualsAndHashCode(callSuper = false) 사용
* */
public class Member extends BaseEntity {//수정 필요

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String phoneNumber;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String name;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String email;

    @Setter
    @JsonIgnore//중요한 정보 숨김. 반환 값에 넣어도 반환이 안됨.
    @NotBlank
    @Column(nullable = false)
    private String password;

    @Setter
    @Column(nullable = false)
    private String memberImageUrl;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private RefreshToken refreshToken;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private FcmToken fcmToken;

    @Builder.Default
    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts=new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Profile> profiles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberRole> memberRoles = new ArrayList<>();

    public void addRole(MemberRole memberRole) {
        memberRoles.add(memberRole);
    }
}
