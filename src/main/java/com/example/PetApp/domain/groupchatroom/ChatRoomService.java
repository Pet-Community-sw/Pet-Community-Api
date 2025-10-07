package com.example.PetApp.domain.groupchatroom;

import com.example.PetApp.domain.groupchatroom.model.dto.request.UpdateChatRoomDto;
import com.example.PetApp.domain.groupchatroom.model.dto.response.ChatMessageResponseDto;
import com.example.PetApp.domain.groupchatroom.model.dto.response.ChatRoomResponseDto;
import com.example.PetApp.domain.groupchatroom.model.dto.response.CreateChatRoomResponseDto;
import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatRoomService {

    List<ChatRoomResponseDto> getChatRooms(Long userId);

    CreateChatRoomResponseDto createChatRoom(WalkingTogetherMatch walkingTogetherMatch, Profile profile);

    void updateChatRoom(Long chatRoomId, UpdateChatRoomDto updateChatRoomDto, Long profileId);

    ChatMessageResponseDto getMessages(Long chatRoomId, Long userId, int page);

    void deleteChatRoom(Long chatRoomId, Long profileId);

    List<Long> getUsers(Long chatRoomId);
}
