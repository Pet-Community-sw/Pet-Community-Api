package com.example.petapp.domain.comment.model;

import com.example.petapp.domain.BaseEntity;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.model.Post;
import com.example.petapp.interfaces.exception.ForbiddenException;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


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
            throw new ForbiddenException("권한이 없습니다.");
        }
    }

    public void update(String content) {
        setContent(content);
    }
}

