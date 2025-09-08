package com.example.PetApp.domain.post.normal.model.entity;

import com.example.PetApp.domain.comment.model.entity.Comment;
import com.example.PetApp.domain.comment.model.entity.Commentable;
import com.example.PetApp.domain.post.common.Post;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("NORMAL")
@PrimaryKeyJoinColumn(name = "post_id")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@SuperBuilder
public class NormalPost extends Post implements Commentable {

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

//    @Override
//    public Like createLike(Member member) {
//        return new NormalPostLike(member, this);
//    }

    @Override
    public List<Comment> getComments() {
        return this.comments;
    }

}
