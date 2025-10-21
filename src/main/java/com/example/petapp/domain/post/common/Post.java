package com.example.petapp.domain.post.common;

import com.example.petapp.common.base.embedded.Content;
import com.example.petapp.common.base.superclass.BaseEntity;
import com.example.petapp.common.exception.ForbiddenException;
import com.example.petapp.domain.like.model.entity.Like;
import com.example.petapp.domain.member.model.entity.Member;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
//@DiscriminatorColumn(name = "post_type")
@SuperBuilder
@Getter
public abstract class Post extends BaseEntity {

    @Embedded
    @Setter
    private Content content;

    @Setter
    private String postImageUrl;// viewCount랑 postImageUrl 명세서에 추가해야함.

    @Setter
    @Min(0)
    @Column(nullable = false)
    private long viewCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @BatchSize(size = 100)
    private List<Like> likes = new ArrayList<>();
//    public abstract Like createLike(Member member);//게시글에서 Like생성 책임 위임

    public void removeLikes(Like like) {
        likes.remove(like);
    }

    public void updateContent(String newTitle, String newContent) {
        content = new Content(newTitle, newContent);
    }

    public void countUpLike(Like like) {
        getLikes().add(like);
    }

    public void validateMember(Member member) {
        if (!(this.member.equals(member))) {
            throw new ForbiddenException("권한 없음.");
        }
    }
}
