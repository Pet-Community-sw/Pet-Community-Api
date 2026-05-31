package com.example.petapp.domain.profile.model;

import com.example.petapp.domain.BaseEntity;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.petbreed.model.PetBreed;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank
    @Column(nullable = false)
    private String petImageUrl;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    @Column(nullable = false)
    private LocalDate petBirthDate;

    @NotBlank
    @Column(nullable = false)
    private String petAge;

    @NotNull
    @JoinColumn(name = "pet_breed_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private PetBreed petBreed;

    @NotBlank
    @Column(nullable = false)
    private String petName;

    @Column(nullable = false)
    private String extraInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "profile_breed",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "pet_breed_id"))
    private Set<PetBreed> avoidBreeds = new HashSet<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WalkingTogetherPost> walkingTogetherPosts;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<ChatRoom> chatRooms;

    public void addAvoidBreeds(PetBreed dogBreed) {
        if (getAvoidBreeds() == null) {
            avoidBreeds = new HashSet<>();
        }
        avoidBreeds.add(dogBreed);
    }

    public void updateProfile(String petName, LocalDate petBirthDate, String extraInfo, String imageFimeName, PetBreed petBreed) {
        this.petImageUrl = "/profile/" + imageFimeName;
        this.petName = petName;
        this.petBirthDate = petBirthDate;
        this.petAge = CalculateAge(petBirthDate) + "살";
        this.petBreed = petBreed;
        this.extraInfo = extraInfo;
        getAvoidBreeds().clear();
    }

}
