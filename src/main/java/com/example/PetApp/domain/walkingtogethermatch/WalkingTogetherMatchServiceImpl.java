package com.example.PetApp.domain.walkingtogethermatch;


import com.example.PetApp.domain.post.recommend.model.entity.RecommendRoutePost;
import com.example.PetApp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import com.example.PetApp.domain.petbreed.model.entity.PetBreed;
import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.domain.groupchatroom.model.dto.response.CreateChatRoomResponseDto;
import com.example.PetApp.domain.walkingtogethermatch.model.dto.request.CreateWalkingTogetherMatchDto;
import com.example.PetApp.domain.walkingtogethermatch.model.dto.response.CreateWalkingTogetherMatchResponseDto;
import com.example.PetApp.domain.walkingtogethermatch.model.dto.response.GetWalkingTogetherMatchResponseDto;
import com.example.PetApp.domain.walkingtogethermatch.model.dto.request.UpdateWalkingTogetherMatchDto;
import com.example.PetApp.common.exception.ConflictException;
import com.example.PetApp.common.exception.ForbiddenException;
import com.example.PetApp.domain.walkingtogethermatch.mapper.WalkingTogetherMatchMapper;
import com.example.PetApp.domain.groupchatroom.ChatRoomService;
import com.example.PetApp.domain.query.QueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class WalkingTogetherMatchServiceImpl implements WalkingTogetherMatchService {

    private final ChatRoomService chatRoomService;
    private final WalkingTogetherMatchRepository walkingTogetherMatchRepository;
    private final QueryService queryService;

    @Transactional(readOnly = true)
    @Override//피해야하는종을 여기서 필터링 하면될듯 피해야하는종에 자신의 종이 포함되어있으면 true를 반환
    public GetWalkingTogetherMatchResponseDto getWalkingTogetherPost(Long walkingTogetherPostId, Long profileId) {
        Profile profile = queryService.findByProfile(profileId);
        WalkingTogetherMatch walkingTogetherMatch = queryService.findByWalkingTogetherPost(walkingTogetherPostId);
        PetBreed petBreed = queryService.findByPetBreed(profile.getPetBreed().getName());

        return WalkingTogetherMatchMapper.toGetWalkingTogetherPostResponseDto(walkingTogetherPostId, walkingTogetherMatch, profile, petBreed);

    }

    @Transactional(readOnly = true)
    @Override
    public List<GetWalkingTogetherMatchResponseDto> getWalkingTogetherPosts(Long recommendRoutePostId, Long profileId) {
        Profile profile = queryService.findByProfile(profileId);
        RecommendRoutePost recommendRoutePost = queryService.findByRecommendRoutePost(recommendRoutePostId);
        PetBreed petBreed = queryService.findByPetBreed(profile.getPetBreed().getName());
        List<WalkingTogetherMatch> walkingTogetherMatches = walkingTogetherMatchRepository.findAllByRecommendRoutePost(recommendRoutePost);
        return WalkingTogetherMatchMapper.toGetWalkingTogetherPostResponseDtos(walkingTogetherMatches, petBreed);
    }

    @Transactional
    @Override
    public CreateWalkingTogetherMatchResponseDto createWalkingTogetherPost(CreateWalkingTogetherMatchDto createWalkingTogetherMatchDto, Long profileId) {
        Profile profile = queryService.findByProfile(profileId);
        RecommendRoutePost recommendRoutePost = queryService.findByRecommendRoutePost(createWalkingTogetherMatchDto.getRecommendRoutePostId());
        WalkingTogetherMatch walkingTogetherMatch = WalkingTogetherMatchMapper.toEntity(profile, recommendRoutePost, createWalkingTogetherMatchDto);
        walkingTogetherMatch.matchingStart(profileId, profile);
        WalkingTogetherMatch savedWalkingTogetherMatch = walkingTogetherMatchRepository.save(walkingTogetherMatch);
        return new CreateWalkingTogetherMatchResponseDto(savedWalkingTogetherMatch.getId());
    }

    @Transactional
    @Override
    public void updateWalkingTogetherPost(Long walkingTogetherPostId, UpdateWalkingTogetherMatchDto updateWalkingTogetherMatchDto, Long profileId) {
        WalkingTogetherMatch walkingTogetherMatch = queryService.findByWalkingTogetherPost(walkingTogetherPostId);
        walkingTogetherMatch.validated(profileId);
        walkingTogetherMatch.update(updateWalkingTogetherMatchDto);
    }

    @Transactional
    @Override
    public void deleteWalkingTogetherPost(Long walkingTogetherPostId, Long profileId) {
        WalkingTogetherMatch walkingTogetherMatch = queryService.findByWalkingTogetherPost(walkingTogetherPostId);
        walkingTogetherMatch.validated(profileId);
        walkingTogetherMatchRepository.delete(walkingTogetherMatch);
    }


    @Transactional
    @Override
    public CreateChatRoomResponseDto startMatch(Long walkingTogetherPostId, Long profileId) {
        Profile profile = queryService.findByProfile(profileId);
        WalkingTogetherMatch walkingTogetherMatch = queryService.findByWalkingTogetherPost(walkingTogetherPostId);
        PetBreed petBreed = queryService.findByPetBreed(profile.getPetBreed().getName());

        walkingTogetherMatch.checkInMatch(profileId, petBreed);
        walkingTogetherMatch.matchingStart(profileId, profile);

        return chatRoomService.createChatRoom(walkingTogetherMatch, profile);
    }
}
