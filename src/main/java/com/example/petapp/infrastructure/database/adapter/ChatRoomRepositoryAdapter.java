package com.example.petapp.infrastructure.database.adapter;

import com.example.petapp.application.in.chatting.model.type.ChatRoomType;
import com.example.petapp.domain.chatroom.ChatRoomRepository;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;
import com.example.petapp.infrastructure.database.jpa.chatroom.JpaChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryAdapter implements ChatRoomRepository {

    private final JpaChatRoomRepository repository;

    @Override
    public int countByProfile(Long id) {
        return repository.countByProfile(id);
    }

    @Override
    public List<ChatRoom> findAll(Long id, ChatRoomType chatRoomType) {
        return repository.findAllByUserIdAndChatRoomType(id, chatRoomType);
    }

    @Override
    public Optional<ChatRoom> find(WalkingTogetherPost walkingTogetherPost) {
        return repository.findByWalkingTogetherPost(walkingTogetherPost);
    }

    @Override
    public boolean existAndContain(Long id, Long userId) {
        return repository.existsByIdAndUsersContains(id, userId);
    }

    @Override
    public ChatRoom save(ChatRoom chatRoom) {
        return repository.save(chatRoom);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<ChatRoom> find(Long id) {
        return repository.findById(id);
    }
}
