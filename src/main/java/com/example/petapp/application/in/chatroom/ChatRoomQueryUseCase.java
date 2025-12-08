package com.example.petapp.application.in.chatroom;

import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.walkingtogethermatch.model.WalkingTogetherMatch;

import java.util.Optional;

public interface ChatRoomQueryUseCase {
    ChatRoom find(Long id);

    Optional<ChatRoom> find(WalkingTogetherMatch walkingTogetherMatch);
}
