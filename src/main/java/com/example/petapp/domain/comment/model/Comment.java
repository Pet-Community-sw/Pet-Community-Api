package com.example.petapp.domain.comment.model;

import com.example.petapp.application.common.exception.ErrorCode;
import com.example.petapp.application.common.exception.PetCommunityException;
import com.example.petapp.domain.BaseEntity;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.model.Post;
import lombok.*;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder//좋아요 어떻게할까
//따로 db에 리스틑 저장안할거임 누른 후 인식만하고 어떤 요청이 있을 때 좋아요 올리기 요청을 보냄?
public class Comment extends BaseEntity {

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String content;

    @Min(0)
    @Setter
    @NotNull
    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private long likeCount;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public void validated(Member member) {
        if (!(getMember().equals(member))) {
            throw new PetCommunityException(ErrorCode.FORBIDDEN, "권한이 없습니다.");
        }
    }

    public void update(String content) {
        setContent(content);
    }
}

