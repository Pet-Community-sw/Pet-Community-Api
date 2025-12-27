package com.example.petapp.infrastructure.database.mongo;


import com.example.petapp.domain.chatting.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MongoChatMessageRepository extends MongoRepository<ChatMessage, String> {

    Optional<ChatMessage> findFirstByChatRoomIdOrderBySeqDesc(Long chatRoomId);

    void deleteByChatRoomId(Long chatRoomId);

    Page<ChatMessage> findAllByChatRoomIdOrderBySeqAsc(Long chatRoomId, Pageable pageable);

    List<ChatMessage> findAllByChatRoomIdAndSeqGreaterThanOrderBySeqAsc(Long chatRoomId, Long seqIsGreaterThan);
}