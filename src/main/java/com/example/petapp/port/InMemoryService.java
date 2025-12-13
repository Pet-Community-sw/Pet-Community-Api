package com.example.petapp.port;


import com.example.petapp.domain.chatting.model.ChatMessage;
import com.example.petapp.domain.chatting.model.dto.LastMessageInfoDto;

import java.util.Set;

public interface InMemoryService {

    void createOnlineData(Long chatroomId, Long profileId);


    Set<String> getOnlineDatas(Long id);


    Boolean existForeGroundData(Long id);

    void createForeGroundData(Long id);


    void createReadData(ChatMessage chatMessage);

    void deleteReadData(Long chatRoomId, Long userId);

    Long getReadData(Long chatRoomId, Long userId);


    void createLastMessageInfoData(ChatMessage chatMessage);

    LastMessageInfoDto getLastMessageInfoData(Long id);

    void deleteLastMessageInfoData(Long chatRoomId);

    void deleteReadData(Long chatRoomId);

    void deleteForeGroundData(Long id);


    boolean existRoomSeq(Long chatRoomId);

    Long incrementSeq(Long chatRoomId);

    void createRoomSeq(Long chatRoomId, Long seq);

    void deleteRoomSeq(Long chatRoomId);
}
