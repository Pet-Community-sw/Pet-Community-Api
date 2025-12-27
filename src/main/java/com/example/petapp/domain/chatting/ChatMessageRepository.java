package com.example.petapp.domain.chatting;


import com.example.petapp.domain.chatting.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository {

    Optional<ChatMessage> findCurrent(Long chatRoomId);

    void delete(Long chatRoomId);

    void save(ChatMessage chatMessage);

    Page<ChatMessage> findAll(Long chatRoomId, Pageable pageable);

    List<ChatMessage> findAllBySeq(Long chatRoomId, Long seqIsGreaterThan);

}

