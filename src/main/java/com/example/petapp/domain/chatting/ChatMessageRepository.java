package com.example.petapp.domain.chatting;


import com.example.petapp.domain.chatting.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    Optional<ChatMessage> findFirstByChatRoomIdOrderBySeqDesc(Long chatRoomId);

    void deleteByChatRoomId(Long chatRoomId);

    Page<ChatMessage> findAllByChatRoomIdOrderBySeqAsc(Long chatRoomId, Pageable pageable);

    List<ChatMessage> findAllByChatRoomIdAndSeqGreaterThanOrderBySeqAsc(Long chatRoomId, Long seqIsGreaterThan);

}

