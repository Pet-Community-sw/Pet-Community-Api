package com.example.PetApp.domain.post.recommend.model.entity;

import com.example.PetApp.domain.comment.model.entity.Comment;
import com.example.PetApp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import com.example.PetApp.infrastructure.database.base.embedded.Location;
import com.example.PetApp.domain.comment.model.entity.Commentable;
import com.example.PetApp.domain.post.common.Post;
import lombok.*;
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

    @OneToMany(mappedBy = "recommendRoutePost",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WalkingTogetherMatch> walkingTogetherMatches;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @Override
    public List<Comment> getComments() {
        return this.comments;
    }

//    @Override
//    public Like createLike(Member member) {
//        return new RecommendRoutePostLike(member, this);
//    }
}
