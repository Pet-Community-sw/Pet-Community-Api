package com.example.petapp.domain.walkrecord.model;

import com.example.petapp.application.common.exception.ErrorCode;
import com.example.petapp.application.common.exception.PetCommunityException;
import com.example.petapp.domain.BaseEntity;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.model.DelegateWalkPost;
import lombok.*;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class WalkRecord extends BaseEntity {

    @NotNull
    @Column(nullable = false)
    private LocalDateTime startTime;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime finishTime;

    @NotNull
    @Column(nullable = false)
    private Double walkDistance;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalkStatus walkStatus = WalkStatus.READY;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegate_walk_post")
    private DelegateWalkPost delegateWalkPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "walk_path_points", joinColumns = @JoinColumn(name = "walk_record_id"))
    @Column(name = "point")
    private List<String> pathPoints = new ArrayList<>();

    public void validatedForCreate(Member member) {
        if (getWalkStatus() != WalkStatus.FINISH) {
            throw new PetCommunityException(ErrorCode.CONFLICT, "산책을 다해야 후기를 작성할 수 있습니다.");
        } else if (!(getMember().equals(member))) {
            throw new PetCommunityException(ErrorCode.FORBIDDEN, "권한이 없습니다.");
        }
    }

    public void updateRecordToPath(Double totalDistance, List<String> paths) {
        walkDistance = totalDistance;
        pathPoints = paths;

    }

    public void validateMember(Long id) {
        if (!getDelegateWalkPost().getSelectedApplicantMemberId().equals(id)) {
            throw new PetCommunityException(ErrorCode.FORBIDDEN, "권한 없음.");
        }
    }

    public void updateWalkStatus(WalkStatus walkStatus) {
        this.walkStatus = walkStatus;
        LocalDateTime now = LocalDateTime.now();
        if (walkStatus == WalkStatus.START) {
            startTime = now;
        } else if (walkStatus == WalkStatus.FINISH) {
            finishTime = now;
        }
    }

    public void validateStart() {
        if (getWalkStatus() != WalkStatus.START) {
            throw new PetCommunityException(ErrorCode.FORBIDDEN, "start 권한 없음.");
        }
    }

}
