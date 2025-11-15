package com.example.petapp.domain.groupchatroom;

import com.example.petapp.domain.groupchatroom.model.dto.request.UpdateChatRoomDto;
import com.example.petapp.domain.groupchatroom.model.dto.response.ChatMessageResponseDto;
import com.example.petapp.domain.groupchatroom.model.dto.response.ChatRoomResponseDto;
import com.example.petapp.domain.groupchatroom.model.dto.response.CreateChatRoomResponseDto;
import com.example.petapp.domain.member.model.entity.Member;
import com.example.petapp.domain.profile.model.entity.Profile;
import com.example.petapp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatRoomService {

    List<ChatRoomResponseDto> getChatRooms(Long userId);

    CreateChatRoomResponseDto createChatRoom(WalkingTogetherMatch walkingTogetherMatch, Profile profile);

    CreateChatRoomResponseDto createChatRoom(Member member, Member applicationMember);

    void updateChatRoom(Long chatRoomId, UpdateChatRoomDto updateChatRoomDto, Long profileId);

    ChatMessageResponseDto getMessages(Long chatRoomId, Long userId, int page);

    void deleteChatRoom(Long chatRoomId, Long profileId);

    List<Long> getUsers(Long chatRoomId);

    ChatMessageResponseDto getAfterMessages(Long chatRoomId, Long lastSeq, Long profileId);
}
