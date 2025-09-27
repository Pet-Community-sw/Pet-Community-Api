package com.example.PetApp.domain.profile.model.entity;

import com.example.PetApp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.PetApp.domain.profile.model.dto.request.ProfileDto;
import com.example.PetApp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.petbreed.model.entity.PetBreed;
import com.example.PetApp.infrastructure.database.base.superclass.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.PetApp.common.util.AgeUtil.CalculateAge;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Profile extends BaseEntity {

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String petImageUrl;

    @Setter
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    @Column(nullable = false)
    private LocalDate petBirthDate;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String petAge;

    @Setter
    @NotBlank
    @JoinColumn(name = "pet_breed_id", nullable = false)
    @OneToOne
    private PetBreed petBreed;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String petName;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String extraInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Setter
    @Builder.Default
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "profile_breed",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "pet_breed_id"))
    private Set<PetBreed> avoidBreeds = new HashSet<>();

    @OneToMany(mappedBy = "profile",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WalkingTogetherMatch> walkingTogetherMatches;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<ChatRoom> chatRooms;

    public void addAvoidBreeds(PetBreed dogBreed) {

        avoidBreeds.add(dogBreed);
    }

    public void updateProfile(Profile profile, ProfileDto profileDto, String imageFimeName, PetBreed petBreed) {
        profile.setPetImageUrl("/profile/" + imageFimeName);
        profile.setPetName(profileDto.getPetName());
        profile.setPetBirthDate(profileDto.getPetBirthDate());
        profile.setPetAge(CalculateAge(profileDto.getPetBirthDate()) + "살");
        profile.setPetBreed(petBreed);
        profile.setExtraInfo(profileDto.getExtraInfo());
        profile.getAvoidBreeds().clear();
    }
}
