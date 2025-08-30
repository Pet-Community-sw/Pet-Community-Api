package com.example.PetApp.domain.like;

import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.like.model.entity.Like;
import com.example.PetApp.domain.post.common.Post;
import com.example.PetApp.domain.like.model.dto.request.LikeCountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

//프로젝션을 통해 한 번 번경해보자
public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("select new com.example.PetApp.common.like.LikeCountDto(l.post.id, count(*)) " +
            "from Like l where l.post.id in :postIds " +
            "group by l.post.id")
    List<LikeCountDto> countByPostIds(@Param("postIds") List<Long> postIds);

    Long countByPost(Post post);

    Boolean existsByPostAndMember(Post post, Member member);
}
