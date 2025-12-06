package com.example.petapp.infrastructure.database.jpa.post;

import com.example.petapp.domain.post.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaPostRepository<T extends Post> extends JpaRepository<T, Long> {

    Page<T> findAllByMemberId(Long memberId, Pageable pageable);

    @Modifying
    @Query("update Post  p set p.viewCount = p.viewCount+1 where p.id = :id")
    void incrementViewCount(@Param("id") Long id);

}
