package com.example.petapp.domain.chatroom;

import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.chatting.model.type.ChatRoomType;
import com.example.petapp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository {
    int countByProfile(Long id);

    List<ChatRoom> findAll(Long id, ChatRoomType chatRoomType);

    Optional<ChatRoom> find(WalkingTogetherMatch walkingTogetherMatch);

    boolean existAndContain(Long id, Long userId);

    ChatRoom save(ChatRoom chatRoom);

    void delete(Long chatRoomId);

    Optional<ChatRoom> find(Long id);
}
