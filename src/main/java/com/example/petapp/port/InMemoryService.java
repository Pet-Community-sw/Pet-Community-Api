package com.example.petapp.port;


import com.example.petapp.domain.chatting.model.ChatMessage;
import com.example.petapp.domain.chatting.model.dto.LastMessageInfoDto;
import com.example.petapp.domain.notification.model.dto.NotificationListDto;

import java.util.List;
import java.util.Set;

public interface InMemoryService {

    void createStringDataWithDuration(String key, String value, long duration);

    String getStringData(String key);

    Boolean existStringData(String key);

    void deleteStringData(String key);

    Set<String> getStringSetData(String userId);
    
    void createStringData(String key, String value);


    List<NotificationListDto> getNotifications(Long key);

    void createNotificationData(Long key, NotificationListDto notificationListDto, int day);


    void createLocationData(String key, String value);

    String getLocationData(Long id);

    List<String> getLocationDatas(Long id);

    void deleteLocationData(Long id);


    void createOnlineData(Long chatroomId, Long profileId);

    void deleteOnlineDate(Long chatRoomId, Long profileId);

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
