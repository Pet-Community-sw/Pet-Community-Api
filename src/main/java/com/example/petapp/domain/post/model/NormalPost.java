package com.example.petapp.domain.post.model;

import com.example.petapp.domain.comment.model.Comment;
import com.example.petapp.domain.comment.model.Commentable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @Override
    public List<Comment> getComments() {
        return comments;
    }

    public void updateNormalPost(String newPostImageUrl, String newTitle, String newContent) {
        updateImage(newPostImageUrl);
        updateContent(newTitle, newContent);
    }
}
