package com.example.petapp.application.service.chatroom;

import com.example.petapp.application.in.chatroom.ChatRoomQueryUseCase;
import com.example.petapp.domain.chatroom.ChatRoomRepository;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.walkingtogethermatch.model.WalkingTogetherMatch;
import com.example.petapp.interfaces.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ChatRoomQueryService implements ChatRoomQueryUseCase {

    private final ChatRoomRepository repository;

    @Override
    public ChatRoom find(Long id) {
        return repository.find(id).orElseThrow(() -> new NotFoundException("해당 채팅방은 없습니다."));
    }

    @Override
    public Optional<ChatRoom> find(WalkingTogetherMatch walkingTogetherMatch) {
        return repository.find(walkingTogetherMatch);
    }

    @Override
    public boolean isExist(Long chatRoomId, Long profileId) {
        return repository.existAndContain(chatRoomId, profileId);
    }
}
