package com.example.PetApp.domain.groupchatroom.model.entity;

import com.example.PetApp.common.exception.ForbiddenException;
import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import com.example.PetApp.common.base.superclass.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@SuperBuilder
public class ChatRoom extends BaseEntity {

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String name;

    @Setter
    @NotNull
    @Column(nullable = false)
    private int limitCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "walking_together_post_id")
    private WalkingTogetherMatch walkingTogetherMatch;

    @Setter
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(joinColumns = @JoinColumn(name = "chat_room_id"),
            inverseJoinColumns = @JoinColumn(name = "profile_id"))
    @Builder.Default
    private List<Profile> profiles=new ArrayList<>();

    public void validateProfile(Profile profile) {
        if (!getProfiles().contains(profile)) {
            throw new ForbiddenException("권한이 없습니다.");
        }
    }

    public void addProfiles(Profile profile) {
        profiles.add(profile);
    }
}