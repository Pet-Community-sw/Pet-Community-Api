package com.example.petapp.application.in.chatroom.mapper;

import com.example.petapp.application.in.chatroom.dto.request.ChatMessageDtoMember;
import com.example.petapp.application.in.chatroom.dto.response.ChatRoomResponseDto;
import com.example.petapp.application.in.profile.dto.response.ChatRoomUsersResponseDto;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.chatting.model.ChatMessage;
import com.example.petapp.domain.chatting.model.dto.LastMessageInfoDto;
import com.example.petapp.domain.chatting.model.type.ChatRoomType;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.walkingtogethermatch.model.WalkingTogetherMatch;

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
                .chatRoomType(ChatRoomType.MANY)//todo : 다시봐야함.
                //이게 수정에서 가능하려나?
                .build();
        chatRoom.addUser(walkingTogetherMatch.getProfile().getId());//글 작성자.
        chatRoom.addUser(profile.getId());//신청하는사람.
        return chatRoom;
    }

    public static ChatRoom toEntity(Member member) {
        return ChatRoom.builder()
                .chatRoomType(ChatRoomType.ONE)
                .name(member.getName() + "님의 방")
                .limitCount(2)
                .build();
    }

    public static ChatRoomUsersResponseDto toChatRoomUsersResponseDto(Profile profile) {
        return ChatRoomUsersResponseDto.builder()
                .userId(profile.getId())
                .userImageUrl(profile.getPetImageUrl())
                .build();
    }


    public static ChatRoomResponseDto toChatRoomsResponseDto(ChatRoom chatRoom, Long userId, LastMessageInfoDto lastMessageInfoDto, long unReadCount, Set<ChatRoomUsersResponseDto> users) {
        return ChatRoomResponseDto.builder()
                .chatRoomId(chatRoom.getId())
                .chatName(chatRoom.getName())
                .userSize(chatRoom.getUsers().size())
                .users(users)
                .lastMessage(lastMessageInfoDto.getLastMessage())
                .unReadCount(unReadCount)
                .lastMessageTime(lastMessageInfoDto.getLastMessageTime().isBlank() ? null : LocalDateTime.parse(lastMessageInfoDto.getLastMessageTime()))
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
