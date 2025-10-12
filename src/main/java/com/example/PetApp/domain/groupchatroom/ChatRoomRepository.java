package com.example.PetApp.domain.groupchatroom;

import com.example.PetApp.domain.chatting.model.type.ChatRoomType;
import com.example.PetApp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.PetApp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("select size(c.users) from ChatRoom c where c.id=:chatRoomId")
    int countByProfile(@Param("chatRoomId") Long chatRoomId);

    @Query("select c from ChatRoom c where :userId member of c.users and c.chatRoomType= :chatRoomType")
    List<ChatRoom> findAllByUserIdAndChatRoomType(@Param("userId") Long userId, @Param("chatRoomType") ChatRoomType chatRoomType);

    Optional<ChatRoom> findByWalkingTogetherMatch(WalkingTogetherMatch walkingTogetherMatch);

    boolean existsByIdAndUsersContains(Long chatRoomId, Long userId);

}
