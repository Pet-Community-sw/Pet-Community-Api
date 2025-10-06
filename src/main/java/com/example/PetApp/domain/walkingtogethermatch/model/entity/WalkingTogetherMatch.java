package com.example.PetApp.domain.walkingtogethermatch.model.entity;

import com.example.PetApp.common.exception.ConflictException;
import com.example.PetApp.common.exception.ForbiddenException;
import com.example.PetApp.domain.petbreed.model.entity.PetBreed;
import com.example.PetApp.domain.post.recommend.model.entity.RecommendRoutePost;
import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.domain.walkingtogethermatch.model.dto.request.UpdateWalkingTogetherMatchDto;
import com.example.PetApp.common.base.superclass.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@SuperBuilder
public class WalkingTogetherMatch extends BaseEntity {

    @Setter
    @NotNull
    @Column(nullable = false)
    private int limitCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommend_route_post_id")
    private RecommendRoutePost recommendRoutePost;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Setter
    @NotNull
    @Column(nullable = false)
    private LocalDateTime scheduledTime;

    @ElementCollection
    @CollectionTable(name = "walking_together_post_profiles")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    @Builder.Default
    private Set<Long> profiles=new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "walking_together_post_avoid_Breeds")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    @Builder.Default
    private Set<Long> avoidBreeds=new HashSet<>();

    public void checkInMatch(Long profileId, PetBreed petBreed) {
        if (getProfiles().contains(profileId)) {
            throw new ConflictException("이미 채팅방에 들어가있습니다.");
        } else if (getAvoidBreeds().contains(petBreed.getId())) {
            throw new ForbiddenException("해당 종은 참여할 수 없습니다.");
        }
    }

    public void validated(Long profileId) {
        if (!(getProfile().getId().equals(profileId))) {
            throw new ForbiddenException("권한이 없습니다.");
        }
    }

    public void update(UpdateWalkingTogetherMatchDto updateWalkingTogetherMatchDto) {
        setScheduledTime(updateWalkingTogetherMatchDto.getScheduledTime());
        setLimitCount(updateWalkingTogetherMatchDto.getLimitCount());
    }

    public void matchingStart(Long profileId, Profile profile) {
        addMatchPostProfiles(profileId);
        addAvoidBreeds(profile);
    }

    public void addMatchPostProfiles(Long profileId) {
        this.profiles.add(profileId);
    }

    public void addAvoidBreeds(Profile profile) {
        profile.getAvoidBreeds().forEach(avoidBreeds -> this.avoidBreeds.add(avoidBreeds.getId()));
    }
}


