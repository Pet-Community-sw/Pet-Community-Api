package com.example.petapp.domain.profile.model;

import com.example.petapp.application.in.profile.dto.request.ProfileDto;
import com.example.petapp.domain.BaseEntity;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.petbreed.model.PetBreed;
import com.example.petapp.domain.walkingtogethermatch.model.WalkingTogetherMatch;
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

import static com.example.petapp.application.common.AgeUtil.CalculateAge;

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
    @NotNull
    @JoinColumn(name = "pet_breed_id", nullable = false)
    @OneToOne
    private PetBreed petBreed;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String petName;

    @Setter
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

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WalkingTogetherMatch> walkingTogetherMatches;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<ChatRoom> chatRooms;

    public void addAvoidBreeds(PetBreed dogBreed) {
        if (getAvoidBreeds() == null) {
            setAvoidBreeds(new HashSet<>());
        }
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
