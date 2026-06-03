package com.example.petapp.domain.member.model;

import com.example.petapp.application.common.exception.ErrorCode;
import com.example.petapp.application.common.exception.PetCommunityException;
import com.example.petapp.domain.BaseEntity;
import com.example.petapp.domain.post.model.Post;
import com.example.petapp.domain.profile.model.Profile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)//JPA 내부에서는 접근 가능하고, 외부에서는 new로 빈 객체 생성 못 하게
@AllArgsConstructor
@SuperBuilder
public class Member extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String phoneNumber;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String email;

    @JsonIgnore//중요한 정보 숨김. 반환 값에 넣어도 반환이 안됨.
    @NotBlank
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String memberImageUrl;

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

    public void updateInfo(String name, String memberImageUrl) {
        this.name = name;
        this.memberImageUrl = memberImageUrl;
    }

    public void validateProfile(Member member, Member profileMember) {
        if (!(member.equals(profileMember))) {
            throw new PetCommunityException(ErrorCode.FORBIDDEN, "권한이 없습니다.");
        }
    }

    public void checkProfileCount() {
        if (profiles.size() >= 4) {
            throw new IllegalStateException("프로필은 최대 4개 입니다.");
        }
    }
}
