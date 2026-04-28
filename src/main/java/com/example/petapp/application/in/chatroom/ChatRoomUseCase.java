package com.example.petapp.application.in.chatroom;

import com.example.petapp.application.in.chatroom.dto.request.UpdateChatRoomDto;
import com.example.petapp.application.in.chatroom.dto.response.ChatMessageResponseDto;
import com.example.petapp.application.in.chatroom.dto.response.ChatRoomResponseDto;
import com.example.petapp.application.in.chatroom.dto.response.CreateChatRoomResponseDto;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;

import java.util.List;
import java.util.Optional;

public interface ChatRoomUseCase {

    List<ChatRoomResponseDto> getChatRooms(Long userId);

    CreateChatRoomResponseDto createChatRoom(WalkingTogetherPost walkingTogetherPost, Profile profile);

    CreateChatRoomResponseDto createChatRoom(Member member, Member applicationMember);

    void updateChatRoom(Long chatRoomId, UpdateChatRoomDto updateChatRoomDto, Long profileId);

    ChatMessageResponseDto getMessages(Long chatRoomId, Long userId, int page);

    void deleteChatRoom(Long chatRoomId, Long profileId);

    List<Long> getUsers(Long chatRoomId);

    ChatMessageResponseDto getAfterMessages(Long chatRoomId, Long lastSeq, Long profileId);

    ChatRoom find(Long id);

    Optional<ChatRoom> find(WalkingTogetherPost walkingTogetherPost);

    boolean isExist(Long chatRoomId, Long profileId);
}
