package com.example.petapp.application.in.chatroom;

import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;

import java.util.Optional;

public interface ChatRoomQueryUseCase {
    ChatRoom find(Long id);

    Optional<ChatRoom> find(WalkingTogetherPost walkingTogetherPost);

    boolean isExist(Long chatRoomId, Long profileId);
}
