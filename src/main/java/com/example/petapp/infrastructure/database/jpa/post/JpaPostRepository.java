package com.example.petapp.infrastructure.database.jpa.post;

import com.example.petapp.application.in.post.normal.dto.response.PostResponseDto;
import com.example.petapp.domain.post.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaPostRepository<T extends Post> extends JpaRepository<T, Long> {

    @Query(value = """
            select new com.example.petapp.application.in.post.normal.dto.response.PostResponseDto(
                p.id, 
                p.postImageUrl, 
                m.id, 
                m.name, 
                m.memberImageUrl, 
                p.createdAt, 
                p.viewCount, 
                p.likeCount, 
                p.content.title,
                (select count(l) > 0 from Like l where l.post = p and l.member.id = :id)
            )
            from Post p 
            join p.member m 
            order by p.id desc
            """,
            countQuery = "select count(p) from Post p"
    )
    Page<PostResponseDto> findList(@Param("id") Long id, Pageable pageable);

    @Query(value = """
            select new com.example.petapp.application.in.post.normal.dto.response.PostResponseDto(
                p.id,
                p.postImageUrl,
                m.id, 
                m.name, 
                m.memberImageUrl, 
                p.createdAt, 
                p.viewCount, 
                p.likeCount, 
                p.content.title,
                (select count(l) > 0 from Like l where l.post = p and l.member.id = :id)
            )
            from Post p 
            join p.member m 
            where m.id = :targetId 
            order by p.id desc
            """,
            countQuery = "select count(p) from Post p where p.member.id = :targetId"
    )
    Page<PostResponseDto> findAllByMemberId(@Param("targetId") Long targetId, @Param("id") Long id, Pageable pageable);

    
    @Modifying
    @Query("update Post  p set p.viewCount = p.viewCount+1 where p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Modifying
    @Query("update Post p set p.likeCount = p.likeCount+1 where p.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("update Post p set p.likeCount = p.likeCount-1 where p.id = :id")
    void decrementLikeCount(@Param("id") Long id);
}
