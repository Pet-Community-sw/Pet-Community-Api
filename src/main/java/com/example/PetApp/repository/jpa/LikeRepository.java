package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.*;
import com.example.PetApp.domain.like.Like;
import com.example.PetApp.domain.post.Post;
import com.example.PetApp.dto.like.LikeCountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

//프로젝션을 통해 한 번 번경해보자
public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("select new com.example.PetApp.dto.like.LikeCountDto(l.post.id, count(*)) " +
            "from Like l where l.post.id in :postIds " +
            "group by l.post.id")
    List<LikeCountDto> countByPostIds(@Param("postIds") List<Long> postIds);

    Long countByPost(Post post);

    Boolean existsByPostAndMember(Post post, Member member);
}
