package com.example.petapp.application.in.match;

import com.example.petapp.application.in.chatroom.dto.response.CreateChatRoomResponseDto;
import com.example.petapp.application.in.match.dto.request.CreateWalkingTogetherMatchDto;
import com.example.petapp.application.in.match.dto.request.UpdateWalkingTogetherMatchDto;
import com.example.petapp.application.in.match.dto.response.CreateWalkingTogetherMatchResponseDto;
import com.example.petapp.application.in.match.dto.response.GetWalkingTogetherMatchResponseDto;

import java.util.List;

public interface WalkingTogetherMatchUseCase {

    CreateWalkingTogetherMatchResponseDto createWalkingTogetherPost(CreateWalkingTogetherMatchDto createWalkingTogetherMatchDto, Long profileId);

    void deleteWalkingTogetherPost(Long walkingTogetherPostId, Long profileId);

    GetWalkingTogetherMatchResponseDto getWalkingTogetherPost(Long walkingTogetherPostId, Long profileId);

    void updateWalkingTogetherPost(Long walkingTogetherPostId, UpdateWalkingTogetherMatchDto updateWalkingTogetherMatchDto, Long profileId);

    CreateChatRoomResponseDto startMatch(Long walkingTogetherPostId, Long profileId);

    List<GetWalkingTogetherMatchResponseDto> getWalkingTogetherPosts(Long walkingTogetherPostId, Long profileId);
}
