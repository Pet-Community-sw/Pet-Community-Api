package com.example.petapp.domain.post.model;

import com.example.petapp.application.in.post.delegate.model.dto.request.UpdateDelegateWalkPostDto;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.interfaces.exception.ConflictException;
import com.example.petapp.interfaces.exception.ForbiddenException;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.Min;
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
    private long price;

    @Setter
    @NotNull
    @Column(nullable = false)
    private Integer allowedRadiusMeters;

    @Setter
    @Column(nullable = true)
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
        if (isRequireProfile()) {
            return member.getProfiles().isEmpty();
        } else
            return false;
    }

    public Set<Applicant> validatedAndGetApplicants(Long profileId) {
        validatedUser(profileId);
        return getApplicants();
    }

    public void validatedUser(Member member) {
        if (!getProfile().getMember().equals(member)) {
            throw new ForbiddenException("권한 없음.");
        }
    }

    public void validatedUser(Long profileId) {
        if (!getProfile().getId().equals(profileId)) {
            throw new ForbiddenException("권한 없음.");
        }
    }

    public boolean hasApplicant(Long memberId) {
        return applicants.stream().noneMatch(applicant -> applicant.getMemberId().equals(memberId));
    }

    public void grantAuthorize() {
        setStartAuthorized(true);
    }

    public void updateDelegateWalkPost(UpdateDelegateWalkPostDto updateDelegateWalkPostDto) {
        setContent(new Content(updateDelegateWalkPostDto.getTitle(), updateDelegateWalkPostDto.getContent()));
        setPrice(updateDelegateWalkPostDto.getPrice());
        setAllowedRadiusMeters(updateDelegateWalkPostDto.getAllowedRedisMeters());
        setRequireProfile(updateDelegateWalkPostDto.isRequireProfile());
        setScheduledTime(updateDelegateWalkPostDto.getScheduledTime());
    }

    public void addApplicant(Member member, String content) {
        getApplicants().add(Applicant.builder()
                .memberId(member.getId())
                .memberName(member.getName())
                .memberImageUrl(member.getMemberImageUrl())
                .content(content)
                .build());
    }

    public void apply(Member member, String content) {
        filtering(member);
        if (!hasApplicant(member.getId())) {
            throw new ConflictException("이미 신청한 회원입니다.");
        } else if (getStatus() == DelegateWalkStatus.COMPLETED) {
            throw new ConflictException("모집 완료 게시글입니다.");
        } else {
            addApplicant(member, content);
        }
    }

    public void validatedAndSelectApplicant(Long selectedMemberId, Member member) {
        validatedUser(member);
        if (hasApplicant(selectedMemberId)) {
            throw new ForbiddenException("권한 없음.");
        }
        setStatus(DelegateWalkStatus.COMPLETED);
        setSelectedApplicantMemberId(selectedMemberId);
    }
}

