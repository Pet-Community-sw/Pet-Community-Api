package com.example.PetApp.domain.chatting;


import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import com.example.PetApp.domain.chatting.model.type.ChatRoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    void deleteByChatRoomId(Long chatRoomId);

    Page<ChatMessage> findAllByChatRoomIdAndChatRoomType(Long chatRoomId, ChatRoomType chatRoomType, Pageable pageable);

    Optional<ChatMessage> findTopByChatRoomIdOrderBySeqDesc(Long chatRoomId);
}

