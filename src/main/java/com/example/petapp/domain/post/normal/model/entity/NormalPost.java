package com.example.petapp.domain.post.normal.model.entity;

import com.example.petapp.domain.comment.model.entity.Comment;
import com.example.petapp.domain.comment.model.entity.Commentable;
import com.example.petapp.domain.member.model.entity.Member;
import com.example.petapp.domain.post.common.Post;
import com.example.petapp.common.base.embedded.Content;
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

    @Override
    public List<Comment> getComments() {
        return this.comments;
    }

    public void updateNormalPost(String newPostImageUrl, String newTitle, String newContent) {
        this.setPostImageUrl(newPostImageUrl);
        this.setContent(new Content(newTitle,newContent));
    }

    public void updateViewCount(Member member) {
        if (!this.getMember().equals(member)) {
            this.setViewCount(this.getViewCount()+1);
        }

    }

}
