package com.example.petapp.infrastructure.database.jpa.chatting;

import com.example.petapp.domain.chatting.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Optional<ChatMessage> findFirstByChatRoomIdOrderBySeqDesc(Long chatRoomId);

    void deleteByChatRoomId(Long chatRoomId);

    Page<ChatMessage> findAllByChatRoomIdOrderBySeqAsc(Long chatRoomId, Pageable pageable);

    List<ChatMessage> findAllByChatRoomIdAndSeqGreaterThanOrderBySeqAsc(Long chatRoomId, Long seqIsGreaterThan);
}
