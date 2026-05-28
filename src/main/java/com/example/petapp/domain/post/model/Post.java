package com.example.petapp.domain.post.model;

import com.example.petapp.application.common.exception.ErrorCode;
import com.example.petapp.application.common.exception.PetCommunityException;
import com.example.petapp.domain.BaseEntity;
import com.example.petapp.domain.like.model.Like;
import com.example.petapp.domain.member.model.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post", indexes = {
        @Index(name = "idx_post_member_id", columnList = "member_id")
})
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
//@DiscriminatorColumn(name = "post_type")
@SuperBuilder
@Getter
public abstract class Post extends BaseEntity {

    @Embedded
    private Content content;

    private String postImageUrl;

    @Min(0)
    @Column(nullable = false)
    private long viewCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Min(0)
    @Column(nullable = false)
    private long likeCount;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Like> likes = new ArrayList<>();
//    public abstract Like createLike(Member member);//게시글에서 Like생성 책임 위임

    public void removeLikes(Like like) {
        likes.remove(like);
    }

    public void updateContent(String newTitle, String newContent) {
        content = new Content(newTitle, newContent);
    }

    public void updateImage(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public void countUpLike(Like like) {
        getLikes().add(like);
    }

    public void validateMember(Member member) {
        if (!(this.member.equals(member))) {
            throw new PetCommunityException(ErrorCode.FORBIDDEN, "권한 없음.");
        }
    }
}
