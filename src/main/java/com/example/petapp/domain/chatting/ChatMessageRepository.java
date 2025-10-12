package com.example.petapp.domain.chatting;


import com.example.petapp.domain.chatting.model.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    void deleteByChatRoomId(Long chatRoomId);

    Page<ChatMessage> findAllByChatRoomId(Long chatRoomId, Pageable pageable);
}

