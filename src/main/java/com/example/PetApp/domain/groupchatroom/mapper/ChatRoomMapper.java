package com.example.PetApp.domain.groupchatroom.mapper;

import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import com.example.PetApp.domain.groupchatroom.model.dto.request.ChatMessageDtoMember;
import com.example.PetApp.domain.groupchatroom.model.dto.response.ChatRoomResponseDto;
import com.example.PetApp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.PetApp.domain.profile.model.dto.response.ChatRoomUsersResponseDto;
import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChatRoomMapper {

    public static ChatRoom toEntity(WalkingTogetherMatch walkingTogetherMatch, Profile profile) {
        ChatRoom chatRoom = ChatRoom.builder()
                .name(walkingTogetherMatch.getProfile().getPetName() + "님의 방")
                .limitCount(walkingTogetherMatch.getLimitCount())//나중에 게시물에서 인원 수를 고정.
                .walkingTogetherMatch(walkingTogetherMatch)
                //이게 수정에서 가능하려나?
                .build();
        chatRoom.addUser(walkingTogetherMatch.getProfile().getId());//글 작성자.
        chatRoom.addUser(profile.getId());//신청하는사람.
        return chatRoom;
    }

    public static ChatRoomResponseDto toChatRoomsResponseDto(ChatRoom chatRoom, Long userId, String lastMessage, int unReadCount, Set<ChatRoomUsersResponseDto> users, LocalDateTime lastMessageTime) {
        return ChatRoomResponseDto.builder()
                .chatRoomId(chatRoom.getId())
                .chatName(chatRoom.getName())
                .userSize(chatRoom.getUsers().size())
                .users(users)
                .lastMessage(lastMessage)
                .unReadCount(unReadCount)
                .lastMessageTime(lastMessageTime)
                .isOwner(chatRoom.getWalkingTogetherMatch().getProfile().getId().equals(userId))
                .build();
    }

    public static List<ChatMessageDtoMember> toChatMessageDtos(List<ChatMessage> chatMessages) {
        return chatMessages.stream()
                .map(chatMessage -> ChatMessageDtoMember.builder()
                        .senderId(chatMessage.getSenderId())
                        .senderName(chatMessage.getSenderName())
                        .senderImageUrl(chatMessage.getSenderImageUrl())
                        .message(chatMessage.getMessage())
                        .unReadCount(chatMessage.getUnReadCount())
                        .messageTime(chatMessage.getMessageTime())
                        .build()
                )
                .collect(Collectors.toList());

    }

}
