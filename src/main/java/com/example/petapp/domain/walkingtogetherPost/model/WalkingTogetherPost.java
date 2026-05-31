package com.example.petapp.domain.walkingtogetherPost.model;

import com.example.petapp.application.common.exception.ErrorCode;
import com.example.petapp.application.common.exception.PetCommunityException;
import com.example.petapp.application.usecase.match.dto.request.UpdateWalkingTogetherPostDto;
import com.example.petapp.domain.BaseEntity;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.petbreed.model.PetBreed;
import com.example.petapp.domain.post.model.RecommendRoutePost;
import com.example.petapp.domain.profile.model.Profile;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cascade;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@SuperBuilder
public class WalkingTogetherPost extends BaseEntity {

    @NotNull
    @Column(nullable = false)
    private int limitCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommend_route_post_id")
    private RecommendRoutePost recommendRoutePost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime scheduledTime;

    @ElementCollection
    @CollectionTable(name = "walking_together_post_profiles")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    @Builder.Default
    private Set<Long> profiles = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "walking_together_post_avoid_Breeds")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    @Builder.Default
    private Set<Long> avoidBreeds = new HashSet<>();

    public void checkInMatch(Long profileId, PetBreed petBreed) {
        if (getProfiles().contains(profileId)) {
            throw new PetCommunityException(ErrorCode.CONFLICT, "이미 채팅방에 들어가있습니다.");
        } else if (getAvoidBreeds().contains(petBreed.getId())) {
            throw new PetCommunityException(ErrorCode.FORBIDDEN, "해당 종은 참여할 수 없습니다.");
        }
    }

    public void validated(Long profileId) {
        if (!(getProfile().getId().equals(profileId))) {
            throw new PetCommunityException(ErrorCode.FORBIDDEN, "권한이 없습니다.");
        }
    }

    public void update(UpdateWalkingTogetherPostDto updateWalkingTogetherPostDto) {
        scheduledTime = updateWalkingTogetherPostDto.getScheduledTime();
        limitCount = updateWalkingTogetherPostDto.getLimitCount();
    }

    public void matchingStart(Long profileId, Profile profile) {
        addMatchPostProfiles(profileId);
        addAvoidBreeds(profile);
    }

    public void addMatchPostProfiles(Long profileId) {
        profiles.add(profileId);
    }

    public void addAvoidBreeds(Profile profile) {
        profile.getAvoidBreeds().forEach(avoidBreeds -> this.avoidBreeds.add(avoidBreeds.getId()));
    }

    public void checkLimitCount(ChatRoom chatRoom) {
        if (limitCount <= chatRoom.getUsers().size()) {
            throw new PetCommunityException(ErrorCode.CONFLICT, "인원초과");
        }
    }
}
