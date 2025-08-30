package com.example.PetApp.domain.walkingtogethermatch;

import com.example.PetApp.domain.groupchatroom.model.dto.response.CreateChatRoomResponseDto;
import com.example.PetApp.domain.walkingtogethermatch.model.dto.request.CreateWalkingTogetherMatchDto;
import com.example.PetApp.domain.walkingtogethermatch.model.dto.response.CreateWalkingTogetherMatchResponseDto;
import com.example.PetApp.domain.walkingtogethermatch.model.dto.response.GetWalkingTogetherMatchResponseDto;
import com.example.PetApp.domain.walkingtogethermatch.model.dto.request.UpdateWalkingTogetherMatchDto;
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
