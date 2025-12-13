package com.example.petapp.port;


import com.example.petapp.domain.chatting.model.ChatMessage;
import com.example.petapp.domain.chatting.model.dto.LastMessageInfoDto;

public interface InMemoryService {
    void createLastMessageInfoData(ChatMessage chatMessage);

    LastMessageInfoDto getLastMessageInfoData(Long id);

    void deleteLastMessageInfoData(Long chatRoomId);


    boolean existRoomSeq(Long chatRoomId);

    Long incrementSeq(Long chatRoomId);

    void createRoomSeq(Long chatRoomId, Long seq);

    void deleteRoomSeq(Long chatRoomId);
}
