package com.example.petapp.domain.post.recommend.model.entity;

import com.example.petapp.common.base.embedded.Location;
import com.example.petapp.domain.comment.model.entity.Comment;
import com.example.petapp.domain.comment.model.entity.Commentable;
import com.example.petapp.domain.post.Post;
import com.example.petapp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("RECOMMEND")
@PrimaryKeyJoinColumn(name = "post_id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@SuperBuilder//상속받은 필드를 사용하기위한 애노테이션
public class RecommendRoutePost extends Post implements Commentable {

    @Embedded
    private Location location;

    @OneToMany(mappedBy = "recommendRoutePost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WalkingTogetherMatch> walkingTogetherMatches;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @Override
    public List<Comment> getComments() {
        return comments;
    }
//    @Override
//    public Like createLike(Member member) {
//        return new RecommendRoutePostLike(member, this);
//    }
}
