package com.example.petapp.infrastructure.database.adapter;

import com.example.petapp.domain.chatmessage.ChatMessageRepository;
import com.example.petapp.domain.chatmessage.model.ChatMessage;
import com.example.petapp.infrastructure.database.jpa.chatting.JpaChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ChatMessageRepositoryAdapter implements ChatMessageRepository {

    private final JpaChatMessageRepository repository;

    @Override
    public void save(ChatMessage chatMessage) {
        repository.save(chatMessage);
    }

    @Override
    public Page<ChatMessage> findAll(Long chatRoomId, Pageable pageable) {
        return repository.findAllByChatRoomIdOrderBySeqAsc(chatRoomId, pageable);
    }

    @Override
    public List<ChatMessage> findAllBySeq(Long chatRoomId, Long seqIsGreaterThan) {
        return repository.findAllByChatRoomIdAndSeqGreaterThanOrderBySeqAsc(chatRoomId, seqIsGreaterThan);
    }

    @Override
    public Optional<ChatMessage> findCurrent(Long chatRoomId) {
        return repository.findFirstByChatRoomIdOrderBySeqDesc(chatRoomId);
    }

    @Override
    public void delete(Long chatRoomId) {
        repository.deleteByChatRoomId(chatRoomId);
    }
}
