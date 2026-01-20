package com.example.petapp.domain.member.model;

import com.example.petapp.domain.BaseEntity;
import com.example.petapp.domain.fcm.model.FcmToken;
import com.example.petapp.domain.post.model.Post;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.token.model.Token;
import com.example.petapp.interfaces.exception.ForbiddenException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private Token token;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private FcmToken fcmToken;

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Profile> profiles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberRole> memberRoles = new ArrayList<>();

    public void addRole(MemberRole memberRole) {
        memberRoles.add(memberRole);
    }

    public boolean isSamePassword(PasswordEncoder passwordEncoder, String newPassword) {
        return passwordEncoder.matches(newPassword, password);
    }

    public void updatePassword(String newPassword) {
        password = newPassword;
    }

    public void validateProfile(Member member, Member profileMember) {
        if (!(member.equals(profileMember))) {
            throw new ForbiddenException("권한이 없습니다.");
        }
    }

    public void checkProfileCount() {
        if (profiles.size() >= 4) {
            throw new IllegalStateException("프로필은 최대 4개 입니다.");
        }
    }
}
