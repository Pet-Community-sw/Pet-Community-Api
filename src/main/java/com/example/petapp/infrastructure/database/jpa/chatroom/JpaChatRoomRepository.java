package com.example.petapp.infrastructure.database.jpa.chatroom;

import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.chatting.model.type.ChatRoomType;
import com.example.petapp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("select size(c.users) from ChatRoom c where c.id=:chatRoomId")
    int countByProfile(@Param("chatRoomId") Long chatRoomId);

    @Query("select c from ChatRoom c where :userId member of c.users and c.chatRoomType= :chatRoomType")
    List<ChatRoom> findAllByUserIdAndChatRoomType(@Param("userId") Long userId, @Param("chatRoomType") ChatRoomType chatRoomType);

    Optional<ChatRoom> findByWalkingTogetherMatch(WalkingTogetherMatch walkingTogetherMatch);

    boolean existsByIdAndUsersContains(Long chatRoomId, Long userId);
}
