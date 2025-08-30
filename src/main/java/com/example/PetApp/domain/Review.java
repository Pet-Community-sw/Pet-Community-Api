package com.example.PetApp.domain;

import com.example.PetApp.domain.embedded.Content;
import com.example.PetApp.domain.superclass.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Review extends BaseEntity {

    public enum ReviewType {
        MEMBER_TO_PROFILE, PROFILE_TO_MEMBER
    }

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "walk_record_id")
    private WalkRecord walkRecord;

    @Setter
    @Embedded
    private Content content;

    @Setter
    @NotNull
    @Column(nullable = false)
    private Integer rating;

    @Enumerated(EnumType.STRING)
    private ReviewType reviewType;
}
