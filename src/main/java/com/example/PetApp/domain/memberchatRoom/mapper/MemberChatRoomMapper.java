package com.example.PetApp.domain.memberchatRoom.mapper;

import com.example.PetApp.domain.memberchatRoom.model.dto.response.MemberChatRoomsResponseDto;

import java.time.LocalDateTime;

public class MemberChatRoomMapper {

    public static MemberChatRoomsResponseDto toMemberChatRoomsResponseDto(String roomName, String roomImageUrl, String lastMessage, String count, String lastMessageTime) {
        int unReadCount = count != null ? Integer.parseInt(count) : 0;
        LocalDateTime lastMessageLocalDateTime = null;
        if (lastMessageTime != null) {
            lastMessageLocalDateTime = LocalDateTime.parse(lastMessageTime);
        }
        return MemberChatRoomsResponseDto.builder()
                .chatName(roomName)
                .chatImageUrl(roomImageUrl)
                .lastMessage(lastMessage)
                .unReadCount(unReadCount)
                .lastMessageTime(lastMessageLocalDateTime)
                .build();
    }


}
