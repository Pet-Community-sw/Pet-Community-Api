package com.example.petapp.domain.like.model;

import com.example.petapp.domain.BaseEntity;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.model.Post;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "like_type")
@Table(name = "likes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"member_id", "post_id"})
        })//같은 유저가 중복방지
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@Getter
public class Like extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

//    public Like(Member member) {
//        this.member = member;
//    }
}
