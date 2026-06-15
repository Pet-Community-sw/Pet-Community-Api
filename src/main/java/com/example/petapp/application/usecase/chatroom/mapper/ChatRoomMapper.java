package com.example.petapp.application.usecase.chatroom.mapper;

import com.example.petapp.application.usecase.chatmessage.model.dto.LastMessageInfoDto;
import com.example.petapp.application.usecase.chatmessage.model.type.ChatRoomType;
import com.example.petapp.application.usecase.chatroom.dto.request.ChatMessageDtoMember;
import com.example.petapp.application.usecase.chatroom.dto.response.ChatRoomResponseDto;
import com.example.petapp.application.usecase.profile.dto.response.ChatRoomUsersResponseDto;
import com.example.petapp.domain.chatmessage.model.ChatMessage;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChatRoomMapper {

    public static ChatRoom toEntity(WalkingTogetherPost walkingTogetherPost, Profile profile) {
        ChatRoom chatRoom = ChatRoom.builder()
                .name(walkingTogetherPost.getProfile().getPetName() + "님의 방")
                .limitCount(walkingTogetherPost.getLimitCount())//나중에 게시물에서 인원 수를 고정.
                .walkingTogetherPost(walkingTogetherPost)
                .chatRoomType(ChatRoomType.MANY)
                .build();
        chatRoom.addUser(walkingTogetherPost.getProfile().getMember());//글 작성자.
        chatRoom.addUser(profile.getMember());
        return chatRoom;
    }

    public static ChatRoom toEntity(Member member) {
        return ChatRoom.builder()
                .chatRoomType(ChatRoomType.ONE)
                .name(member.getName() + "님의 방")
                .limitCount(2)
                .build();
    }

    public static ChatRoomUsersResponseDto toChatRoomUsersResponseDto(Member member) {
        return ChatRoomUsersResponseDto.builder()
                .userId(member.getId())
                .userImageUrl(member.getMemberImageUrl())
                .build();
    }


    public static ChatRoomResponseDto toChatRoomsResponseDto(ChatRoom chatRoom, Long memberId, LastMessageInfoDto lastMessageInfoDto, Set<ChatRoomUsersResponseDto> users) {
        return ChatRoomResponseDto.builder()
                .chatRoomId(chatRoom.getId())
                .chatName(chatRoom.getName())
                .userSize(chatRoom.getUsers().size())
                .users(users)
                .lastMessage(lastMessageInfoDto.getLastMessage())
                .lastMessageTime(lastMessageInfoDto.getLastMessageTime().isBlank() ? null : LocalDateTime.parse(lastMessageInfoDto.getLastMessageTime()))
                .isOwner(chatRoom.getWalkingTogetherPost().getProfile().getMember().getId().equals(memberId))
                .build();
    }

    public static List<ChatMessageDtoMember> toChatMessageDtos(List<ChatMessage> chatMessages) {
        return chatMessages.stream()
                .map(chatMessage -> ChatMessageDtoMember.builder()
                        .senderId(chatMessage.getSenderId())
                        .senderName(chatMessage.getSenderName())
                        .senderImageUrl(chatMessage.getSenderImageUrl())
                        .message(chatMessage.getMessage())
                        .messageTime(chatMessage.getMessageTime())
                        .build()
                )
                .collect(Collectors.toList());

    }

}
