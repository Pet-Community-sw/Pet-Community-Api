package com.example.petapp.domain.post.model;

import com.example.petapp.application.common.exception.ErrorCode;
import com.example.petapp.application.common.exception.PetCommunityException;
import com.example.petapp.application.usecase.post.delegate.model.dto.request.UpdateDelegateWalkPostDto;
import com.example.petapp.domain.comment.model.Comment;
import com.example.petapp.domain.comment.model.Commentable;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.model.Profile;
import lombok.*;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@DiscriminatorValue("DELEGATE")
@PrimaryKeyJoinColumn(name = "post_id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@AllArgsConstructor
public class DelegateWalkPost extends Post implements Commentable {

    @Embedded
    private Location location;

    @Min(0)
    @NotNull
    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private long price;

    @NotNull
    @Column(nullable = false)
    private Integer allowedRadiusMeters;

    @Column(nullable = true)
    private Long selectedApplicantMemberId;

    @NotNull
    @Column(nullable = false)
    private boolean requireProfile;//profile여부 true or false

    @NotNull
    @Column(nullable = false)
    private boolean startAuthorized;// start권한 부여

    @Column(nullable = false)
    private LocalDateTime scheduledTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DelegateWalkStatus status = DelegateWalkStatus.RECRUITING;//기본값을 모집중으로 선언.

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "walker_post_applicants")
    private Set<Applicant> applicants = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @Override
    public List<Comment> getComments() {
        return comments;
    }

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
            throw new PetCommunityException(ErrorCode.FORBIDDEN, "권한 없음.");
        }
    }

    public void validatedUser(Long profileId) {
        if (!getProfile().getId().equals(profileId)) {
            throw new PetCommunityException(ErrorCode.FORBIDDEN, "권한 없음.");
        }
    }

    public boolean hasApplicant(Long memberId) {
        return applicants.stream().noneMatch(applicant -> applicant.getMemberId().equals(memberId));
    }

    public void grantAuthorize() {
        startAuthorized = true;
    }

    public void updateDelegateWalkPost(UpdateDelegateWalkPostDto updateDelegateWalkPostDto) {
        updateContent(updateDelegateWalkPostDto.getTitle(), updateDelegateWalkPostDto.getContent());
        price = updateDelegateWalkPostDto.getPrice();
        allowedRadiusMeters = updateDelegateWalkPostDto.getAllowedRedisMeters();
        requireProfile = updateDelegateWalkPostDto.isRequireProfile();
        scheduledTime = updateDelegateWalkPostDto.getScheduledTime();
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
            throw new PetCommunityException(ErrorCode.CONFLICT, "이미 신청한 회원입니다.");
        } else if (getStatus() == DelegateWalkStatus.COMPLETED) {
            throw new PetCommunityException(ErrorCode.CONFLICT, "모집 완료 게시글입니다.");
        } else {
            addApplicant(member, content);
        }
    }

    public void validatedAndSelectApplicant(Long selectedMemberId, Member member) {
        validatedUser(member);
        if (hasApplicant(selectedMemberId)) {
            throw new PetCommunityException(ErrorCode.FORBIDDEN, "권한 없음.");
        }
        status = DelegateWalkStatus.COMPLETED;
        selectedApplicantMemberId = selectedMemberId;
    }
}
