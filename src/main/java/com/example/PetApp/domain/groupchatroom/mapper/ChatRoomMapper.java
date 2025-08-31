package com.example.PetApp.domain.groupchatroom.mapper;

import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import com.example.PetApp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import com.example.PetApp.domain.groupchatroom.model.dto.request.ChatMessageDto;
import com.example.PetApp.domain.groupchatroom.model.dto.response.ChatRoomsResponseDto;
import com.example.PetApp.domain.groupchatroom.model.dto.request.UpdateChatUnReadCountDto;
import com.example.PetApp.domain.profile.model.dto.response.ChatRoomProfilesResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ChatRoomMapper {

    public static ChatRoom toEntity(WalkingTogetherMatch walkingTogetherMatch, Profile profile) {
        ChatRoom chatRoom = ChatRoom.builder()
                .name(walkingTogetherMatch.getProfile().getPetName()+"님의 방")
                .limitCount(walkingTogetherMatch.getLimitCount())//나중에 게시물에서 인원 수를 고정.
                .walkingTogetherMatch(walkingTogetherMatch)
                //이게 수정에서 가능하려나?
                .build();
        chatRoom.addProfiles(walkingTogetherMatch.getProfile());//글 작성자.
        chatRoom.addProfiles(profile);//신청하는사람.
        return chatRoom;
    }

    public static ChatRoomsResponseDto toChatRoomsResponseDto(ChatRoom chatRoom, Long profileId, String lastMessage, int unReadCount, LocalDateTime lastMessageTime) {
        return ChatRoomsResponseDto.builder()
                .chatRoomId(chatRoom.getId())
                .chatName(chatRoom.getName())
                .chatLimitCount(chatRoom.getLimitCount())
                .currentCount(chatRoom.getProfiles().size())
                .chatRoomTime(chatRoom.getCreatedAt())
                .profiles(
                        chatRoom.getProfiles().stream()
                                .map(profile -> ChatRoomProfilesResponseDto.builder()
                                        .profileId(profile.getId())
                                        .profileImageUrl(profile.getPetImageUrl())
                                        .build())
                                .collect(Collectors.toSet())
                )
                .lastMessage(lastMessage)
                .unReadCount(unReadCount)
                .lastMessageTime(lastMessageTime)
                .isOwner(chatRoom.getWalkingTogetherMatch().getProfile().getId().equals(profileId))
                .build();
    }

    public static List<ChatMessageDto> toChatMessageDtos(List<ChatMessage> chatMessages) {
        return chatMessages.stream()
                .map(chatMessage -> ChatMessageDto.builder()
                        .senderId(chatMessage.getSenderId())
                        .senderName(chatMessage.getSenderName())
                        .senderImageUrl(chatMessage.getSenderImageUrl())
                        .message(chatMessage.getMessage())
                        .unReadCount(chatMessage.getChatUnReadCount())
                        .messageTime(chatMessage.getMessageTime())
                        .build()
                )
                .collect(Collectors.toList());

    }

    public static UpdateChatUnReadCountDto toUpdateChatUnReadCountDto(ChatMessage chatMessage) {
        return UpdateChatUnReadCountDto.builder()
                .chatRoomId(chatMessage.getChatRoomId())
                .id(chatMessage.getId())
                .chatUnReadCount(chatMessage.getChatUnReadCount())
                .build();
    }

}
