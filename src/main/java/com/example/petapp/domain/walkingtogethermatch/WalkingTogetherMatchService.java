package com.example.petapp.domain.walkingtogethermatch;

import com.example.petapp.application.in.chatroom.dto.response.CreateChatRoomResponseDto;
import com.example.petapp.domain.walkingtogethermatch.model.dto.request.CreateWalkingTogetherMatchDto;
import com.example.petapp.domain.walkingtogethermatch.model.dto.request.UpdateWalkingTogetherMatchDto;
import com.example.petapp.domain.walkingtogethermatch.model.dto.response.CreateWalkingTogetherMatchResponseDto;
import com.example.petapp.domain.walkingtogethermatch.model.dto.response.GetWalkingTogetherMatchResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WalkingTogetherMatchService {

    CreateWalkingTogetherMatchResponseDto createWalkingTogetherPost(CreateWalkingTogetherMatchDto createWalkingTogetherMatchDto, Long profileId);

    void deleteWalkingTogetherPost(Long walkingTogetherPostId, Long profileId);

    GetWalkingTogetherMatchResponseDto getWalkingTogetherPost(Long walkingTogetherPostId, Long profileId);

    void updateWalkingTogetherPost(Long walkingTogetherPostId, UpdateWalkingTogetherMatchDto updateWalkingTogetherMatchDto, Long profileId);

    CreateChatRoomResponseDto startMatch(Long walkingTogetherPostId, Long profileId);

    List<GetWalkingTogetherMatchResponseDto> getWalkingTogetherPosts(Long walkingTogetherPostId, Long profileId);
}
