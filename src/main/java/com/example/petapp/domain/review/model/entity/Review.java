package com.example.petapp.domain.review.model.entity;

import com.example.petapp.common.base.embedded.Content;
import com.example.petapp.common.base.superclass.BaseEntity;
import com.example.petapp.common.exception.ForbiddenException;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.model.entity.Profile;
import com.example.petapp.domain.review.model.dto.request.UpdateReviewDto;
import com.example.petapp.domain.walkrecord.model.entity.WalkRecord;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Review extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;
    @ManyToOne
    @JoinColumn(name = "walk_record_id")
    private WalkRecord walkRecord;
    @Setter
    @Embedded
    private Content content;
    @Setter
    @NotNull
    @Column(nullable = false)
    private Integer rating;
    @Enumerated(EnumType.STRING)
    private ReviewType reviewType;

    public void update(UpdateReviewDto updateReviewDto) {
        setContent(new Content(updateReviewDto.getTitle(), updateReviewDto.getContent()));
        setRating(updateReviewDto.getRating());
    }

    public void validated(Member member) {
        if (getReviewType() == ReviewType.MEMBER_TO_PROFILE) {
            if (!getMember().equals(member)) {
                throw new ForbiddenException("권한이 없습니다.");
            }
        } else if (getReviewType() == ReviewType.PROFILE_TO_MEMBER) {
            if (!getProfile().getMember().equals(member)) {
                throw new ForbiddenException("권한이 없습니다.");
            }
        }
    }

    public enum ReviewType {
        MEMBER_TO_PROFILE, PROFILE_TO_MEMBER
    }
}
