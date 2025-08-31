package com.example.PetApp.domain.groupchatroom;

import com.example.PetApp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.PetApp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import com.example.PetApp.domain.profile.model.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("select size( c.profiles) from ChatRoom c where c.id=:chatRoomId")
    int countByProfile(@Param("chatRoomId") Long chatRoomId);

    List<ChatRoom> findAllByProfilesContains(Profile profile);// 이거 검사해봐야할듯.

    Optional<ChatRoom> findByWalkingTogetherMatch(WalkingTogetherMatch walkingTogetherMatch);

    boolean existsByIdAndProfilesContains(Long chatRoomId, Profile profile);

}
