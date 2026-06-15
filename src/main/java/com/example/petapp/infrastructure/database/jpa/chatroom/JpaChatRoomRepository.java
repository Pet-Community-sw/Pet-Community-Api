package com.example.petapp.infrastructure.database.jpa.chatroom;

import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("select size(c.users) from ChatRoom c where c.id=:chatRoomId")
    int countByProfile(@Param("chatRoomId") Long chatRoomId);

    @Query("""
            select c
            from ChatRoom c
            join c.users u
            where u.id = :memberId
            """)
    List<ChatRoom> findAllByMemberId(@Param("memberId") Long memberId);

    Optional<ChatRoom> findByWalkingTogetherPost(WalkingTogetherPost walkingTogetherPost);

    @Query("""
            select count(c) > 0
            from ChatRoom c
            join c.users u
            where c.id = :chatRoomId
              and u.id = :memberId
            """)
    boolean existsByIdAndUsersContains(@Param("chatRoomId") Long chatRoomId, @Param("memberId") Long memberId);
}
