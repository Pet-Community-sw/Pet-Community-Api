package com.example.PetApp.domain.walkrecord.model.entity;

import com.example.PetApp.common.exception.ConflictException;
import com.example.PetApp.common.exception.ForbiddenException;
import com.example.PetApp.domain.post.delegate.model.entity.DelegateWalkPost;
import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.infrastructure.database.base.superclass.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class WalkRecord extends BaseEntity {

    public enum WalkStatus {
        READY, START, FINISH, CANCELED
    }

    @Setter
    @NotNull
    @Column(nullable = false)
    private LocalDateTime startTime;

    @Setter
    @NotNull
    @Column(nullable = false)
    private LocalDateTime finishTime;

    @Setter
    @NotNull
    @Column(nullable = false)
    private Double walkDistance;

    @Setter
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

    @Setter
    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "walk_path_points", joinColumns = @JoinColumn(name = "walk_record_id"))
    @Column(name = "point")
    private List<String> pathPoints = new ArrayList<>();

    public void validatedForCreate(Member member) {
        if (getWalkStatus() != WalkRecord.WalkStatus.FINISH) {
            throw new ConflictException("산책을 다해야 후기를 작성할 수 있습니다.");
        } else if (!(getMember().equals(member))) {
            throw new ForbiddenException("권한이 없습니다.");
        }
    }

    public void updateRecordToPath(Double totalDistance, List<String> paths) {
        setWalkDistance(totalDistance);
        setPathPoints(paths);

    }

    public void validateMember(Member member) {
        if (!getDelegateWalkPost().getProfile().getMember().equals(member)) {
            throw new ForbiddenException("권한 없음.");
        }
    }

    public void validateMember(Long id) {
        if (getDelegateWalkPost().getSelectedApplicantMemberId().equals(id)) {
            throw new ForbiddenException("권한 없음.");
        }
    }

    public void updateWalkStatus(WalkStatus walkStatus) {
        setWalkStatus(walkStatus);
        setStartTime(LocalDateTime.now());
    }
}
