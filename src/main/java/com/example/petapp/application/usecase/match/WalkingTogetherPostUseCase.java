package com.example.petapp.application.usecase.match;

import com.example.petapp.application.usecase.chatroom.dto.response.CreateChatRoomResponseDto;
import com.example.petapp.application.usecase.match.dto.request.CreateWalkingTogetherPostDto;
import com.example.petapp.application.usecase.match.dto.request.UpdateWalkingTogetherPostDto;
import com.example.petapp.application.usecase.match.dto.response.CreateWalkingTogetherPostResponseDto;
import com.example.petapp.application.usecase.match.dto.response.GetWalkingTogetherPostResponseDto;

import java.util.List;

public interface WalkingTogetherPostUseCase {

    CreateWalkingTogetherPostResponseDto createWalkingTogetherPost(CreateWalkingTogetherPostDto createWalkingTogetherPostDto, Long profileId);

    void deleteWalkingTogetherPost(Long walkingTogetherPostId, Long profileId);

    GetWalkingTogetherPostResponseDto getWalkingTogetherPost(Long walkingTogetherPostId, Long profileId);

    void updateWalkingTogetherPost(Long walkingTogetherPostId, UpdateWalkingTogetherPostDto updateWalkingTogetherPostDto, Long profileId);

    CreateChatRoomResponseDto startMatch(Long walkingTogetherPostId, Long profileId);

    List<GetWalkingTogetherPostResponseDto> getWalkingTogetherPosts(Long walkingTogetherPostId, Long profileId);
}
