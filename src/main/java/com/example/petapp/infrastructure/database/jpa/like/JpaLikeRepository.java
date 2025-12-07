package com.example.petapp.infrastructure.database.jpa.like;

import com.example.petapp.application.in.like.dto.request.LikeCountDto;
import com.example.petapp.domain.like.model.Like;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaLikeRepository extends JpaRepository<Like, Long> {
    @Query("select new com.example.petapp.application.in.like.dto.request.LikeCountDto(l.post.id, count(*)) " +
            "from Like l where l.post.id in :postIds " +
            "group by l.post.id")
    List<LikeCountDto> countByPostIds(@Param("postIds") List<Long> postIds);

    Long countByPost(Post post);

    Boolean existsByPostAndMember(Post post, Member member);
}
