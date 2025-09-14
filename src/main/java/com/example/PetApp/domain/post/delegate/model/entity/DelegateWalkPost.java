package com.example.PetApp.domain.post.delegate.model.entity;

import com.example.PetApp.common.exception.ConflictException;
import com.example.PetApp.common.exception.ForbiddenException;
import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.post.delegate.model.dto.request.UpdateDelegateWalkPostDto;
import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.infrastructure.database.shared.embedded.Applicant;
import com.example.PetApp.infrastructure.database.shared.embedded.Content;
import com.example.PetApp.infrastructure.database.shared.embedded.Location;
import com.example.PetApp.domain.post.common.Post;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("DELEGATE")
@PrimaryKeyJoinColumn(name = "post_id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@AllArgsConstructor
public class DelegateWalkPost extends Post {

    @Setter
    @Embedded
    private Location location;

    @Setter
    @Min(0)
    @NotNull
    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long price;

    @Setter
    @NotNull
    @Column(nullable = false)
    private Integer allowedRadiusMeters;

    @Setter
    @NotEmpty
    @Column(nullable = false)
    private Long selectedApplicantMemberId;

    @Setter
    @NotNull
    @Column(nullable = false)
    private boolean requireProfile;//profile여부 true or false

    @Setter
    @NotNull
    @Column(nullable = false)
    private boolean startAuthorized;// start권한 부여

    @Setter
    @NotEmpty
    @Column(nullable = false)
    private LocalDateTime scheduledTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DelegateWalkStatus status = DelegateWalkStatus.RECRUITING;//기본값을 모집중으로 선언.

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "walker_post_applicants")
    private Set<Applicant> applicants = new HashSet<>();

    public boolean filtering(Member member) {
        if (this.isRequireProfile()) {
            return member.getProfiles().isEmpty();
        } else
            return false;
    }

    public boolean validatedUser(Member member) {
        return !this.getProfile().getMember().equals(member);
    }

    public boolean validatedUser(Long profileId) {
        return !this.getProfile().getId().equals(profileId);
    }

    public boolean validatedApplicantsInMember(Long memberId) {
        return this.applicants.stream().noneMatch(applicant -> applicant.getMemberId().equals(memberId));
    }

    public void updateStatusAndSelectedApplicantMemberId(Long selectedMemberId) {
        this.setStatus(DelegateWalkStatus.COMPLETED);
        this.setSelectedApplicantMemberId(selectedMemberId);
    }

    public void grantAuthorize() {
        this.setStartAuthorized(true);
    }

    public void updateDelegateWalkPost(UpdateDelegateWalkPostDto updateDelegateWalkPostDto) {
        this.setContent(new Content(updateDelegateWalkPostDto.getTitle(), updateDelegateWalkPostDto.getContent()));
        this.setPrice(updateDelegateWalkPostDto.getPrice());
        this.setAllowedRadiusMeters(updateDelegateWalkPostDto.getAllowedRedisMeters());
        this.setRequireProfile(updateDelegateWalkPostDto.isRequireProfile());
        this.setScheduledTime(updateDelegateWalkPostDto.getScheduledTime());
    }

    public void addApplicant(Member member, String content) {
        this.getApplicants().add(Applicant.builder()
                .memberId(member.getId())
                .content(content)
                .build());
    }

    public void apply( Member member, String content) {
        if (this.filtering(member)) {
            throw new ForbiddenException("프로필 등록해주세요.");
        } else if (this.validatedApplicantsInMember(member.getId())) {
            throw new ConflictException("이미 신청한 회원입니다.");
        } else if (this.getStatus() == DelegateWalkStatus.COMPLETED) {
            throw new ConflictException("모집 완료 게시글입니다.");
        }else {
            this.addApplicant(member, content);
        }
    }
}

