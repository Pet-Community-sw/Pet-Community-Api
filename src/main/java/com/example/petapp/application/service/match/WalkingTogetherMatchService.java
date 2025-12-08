package com.example.petapp.application.service.match;


import com.example.petapp.application.in.chatroom.ChatRoomUseCase;
import com.example.petapp.application.in.chatroom.dto.response.CreateChatRoomResponseDto;
import com.example.petapp.application.in.match.WalkingTogetherMatchQueryUseCase;
import com.example.petapp.application.in.match.WalkingTogetherMatchUseCase;
import com.example.petapp.application.in.match.dto.request.CreateWalkingTogetherMatchDto;
import com.example.petapp.application.in.match.dto.request.UpdateWalkingTogetherMatchDto;
import com.example.petapp.application.in.match.dto.response.CreateWalkingTogetherMatchResponseDto;
import com.example.petapp.application.in.match.dto.response.GetWalkingTogetherMatchResponseDto;
import com.example.petapp.application.in.match.mapper.WalkingTogetherMatchMapper;
import com.example.petapp.application.in.petbreed.PetBreedQueryUseCase;
import com.example.petapp.application.in.post.PostQueryUseCase;
import com.example.petapp.application.in.profile.ProfileQueryUseCase;
import com.example.petapp.domain.petbreed.model.PetBreed;
import com.example.petapp.domain.post.model.RecommendRoutePost;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.walkingtogethermatch.WalkingTogetherMatchRepository;
import com.example.petapp.domain.walkingtogethermatch.model.WalkingTogetherMatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalkingTogetherMatchService implements WalkingTogetherMatchUseCase {

    private final ChatRoomUseCase chatRoomUseCase;
    private final WalkingTogetherMatchRepository walkingTogetherMatchRepository;
    private final WalkingTogetherMatchQueryUseCase walkingTogetherMatchQueryUseCase;
    private final PetBreedQueryUseCase petBreedQueryUseCase;
    private final ProfileQueryUseCase profileQueryUseCase;
    private final PostQueryUseCase<RecommendRoutePost> postQueryUseCase;

    @Transactional(readOnly = true)
    @Override//피해야하는종을 여기서 필터링 하면될듯 피해야하는종에 자신의 종이 포함되어있으면 true를 반환
    public GetWalkingTogetherMatchResponseDto getWalkingTogetherPost(Long walkingTogetherPostId, Long profileId) {
        Profile profile = profileQueryUseCase.findOrThrow(profileId);
        WalkingTogetherMatch walkingTogetherMatch = walkingTogetherMatchQueryUseCase.findOrThrow(walkingTogetherPostId);
        PetBreed petBreed = petBreedQueryUseCase.find(profile.getPetBreed().getName());

        return WalkingTogetherMatchMapper.toGetWalkingTogetherPostResponseDto(walkingTogetherPostId, walkingTogetherMatch, profile, petBreed);

    }

    @Transactional(readOnly = true)
    @Override
    public List<GetWalkingTogetherMatchResponseDto> getWalkingTogetherPosts(Long recommendRoutePostId, Long profileId) {
        Profile profile = profileQueryUseCase.findOrThrow(profileId);
        RecommendRoutePost recommendRoutePost = postQueryUseCase.findOrThrow(recommendRoutePostId);
        PetBreed petBreed = petBreedQueryUseCase.find(profile.getPetBreed().getName());
        List<WalkingTogetherMatch> walkingTogetherMatches = walkingTogetherMatchRepository.findAllByRecommendRoutePost(recommendRoutePost);
        return WalkingTogetherMatchMapper.toGetWalkingTogetherPostResponseDtos(walkingTogetherMatches, petBreed);
    }

    @Transactional
    @Override
    public CreateWalkingTogetherMatchResponseDto createWalkingTogetherPost(CreateWalkingTogetherMatchDto createWalkingTogetherMatchDto, Long profileId) {
        Profile profile = profileQueryUseCase.findOrThrow(profileId);
        RecommendRoutePost recommendRoutePost = postQueryUseCase.findOrThrow(createWalkingTogetherMatchDto.getRecommendRoutePostId());
        WalkingTogetherMatch walkingTogetherMatch = WalkingTogetherMatchMapper.toEntity(profile, recommendRoutePost, createWalkingTogetherMatchDto);
        walkingTogetherMatch.matchingStart(profileId, profile);
        WalkingTogetherMatch savedWalkingTogetherMatch = walkingTogetherMatchRepository.save(walkingTogetherMatch);
        return new CreateWalkingTogetherMatchResponseDto(savedWalkingTogetherMatch.getId());
    }

    @Transactional
    @Override
    public void updateWalkingTogetherPost(Long walkingTogetherPostId, UpdateWalkingTogetherMatchDto updateWalkingTogetherMatchDto, Long profileId) {
        WalkingTogetherMatch walkingTogetherMatch = walkingTogetherMatchQueryUseCase.findOrThrow(walkingTogetherPostId);
        walkingTogetherMatch.validated(profileId);
        walkingTogetherMatch.update(updateWalkingTogetherMatchDto);
    }

    @Transactional
    @Override
    public void deleteWalkingTogetherPost(Long walkingTogetherPostId, Long profileId) {
        WalkingTogetherMatch walkingTogetherMatch = walkingTogetherMatchQueryUseCase.findOrThrow(walkingTogetherPostId);
        walkingTogetherMatch.validated(profileId);
        walkingTogetherMatchRepository.delete(walkingTogetherMatch);
    }


    @Transactional
    @Override
    public CreateChatRoomResponseDto startMatch(Long walkingTogetherPostId, Long profileId) {
        Profile profile = profileQueryUseCase.findOrThrow(profileId);
        WalkingTogetherMatch walkingTogetherMatch = walkingTogetherMatchQueryUseCase.findOrThrow(walkingTogetherPostId);
        PetBreed petBreed = petBreedQueryUseCase.find(profile.getPetBreed().getName());

        walkingTogetherMatch.checkInMatch(profileId, petBreed);
        walkingTogetherMatch.matchingStart(profileId, profile);

        return chatRoomUseCase.createChatRoom(walkingTogetherMatch, profile);
    }
}
