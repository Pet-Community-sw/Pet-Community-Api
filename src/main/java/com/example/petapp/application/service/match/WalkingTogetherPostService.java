package com.example.petapp.application.service.match;


import com.example.petapp.application.in.chatroom.ChatRoomUseCase;
import com.example.petapp.application.in.chatroom.dto.response.CreateChatRoomResponseDto;
import com.example.petapp.application.in.match.WalkingTogetherPostQueryUseCase;
import com.example.petapp.application.in.match.WalkingTogetherPostUseCase;
import com.example.petapp.application.in.match.dto.request.CreateWalkingTogetherPostDto;
import com.example.petapp.application.in.match.dto.request.UpdateWalkingTogetherPostDto;
import com.example.petapp.application.in.match.dto.response.CreateWalkingTogetherPostResponseDto;
import com.example.petapp.application.in.match.dto.response.GetWalkingTogetherPostResponseDto;
import com.example.petapp.application.in.match.mapper.WalkingTogetherPostMapper;
import com.example.petapp.application.in.petbreed.PetBreedQueryUseCase;
import com.example.petapp.application.in.post.PostQueryUseCase;
import com.example.petapp.application.in.profile.ProfileQueryUseCase;
import com.example.petapp.domain.petbreed.model.PetBreed;
import com.example.petapp.domain.post.model.RecommendRoutePost;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.walkingtogetherPost.WalkingTogetherPostRepository;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalkingTogetherPostService implements WalkingTogetherPostUseCase {

    private final ChatRoomUseCase chatRoomUseCase;
    private final WalkingTogetherPostRepository walkingTogetherPostRepository;
    private final WalkingTogetherPostQueryUseCase walkingTogetherPostQueryUseCase;
    private final PetBreedQueryUseCase petBreedQueryUseCase;
    private final ProfileQueryUseCase profileQueryUseCase;
    private final PostQueryUseCase<RecommendRoutePost> postQueryUseCase;

    @Transactional(readOnly = true)
    @Override//피해야하는종을 여기서 필터링 하면될듯 피해야하는종에 자신의 종이 포함되어있으면 true를 반환
    public GetWalkingTogetherPostResponseDto getWalkingTogetherPost(Long walkingTogetherPostId, Long profileId) {
        Profile profile = profileQueryUseCase.findOrThrow(profileId);
        WalkingTogetherPost walkingTogetherPost = walkingTogetherPostQueryUseCase.findOrThrow(walkingTogetherPostId);
        PetBreed petBreed = petBreedQueryUseCase.findOrThrow(profile.getPetBreed().getName());

        return WalkingTogetherPostMapper.toGetWalkingTogetherPostResponseDto(walkingTogetherPostId, walkingTogetherPost, profile, petBreed);

    }

    @Transactional(readOnly = true)
    @Override
    public List<GetWalkingTogetherPostResponseDto> getWalkingTogetherPosts(Long recommendRoutePostId, Long profileId) {
        Profile profile = profileQueryUseCase.findOrThrow(profileId);
        RecommendRoutePost recommendRoutePost = postQueryUseCase.findOrThrow(recommendRoutePostId);
        PetBreed petBreed = petBreedQueryUseCase.findOrThrow(profile.getPetBreed().getName());
        List<WalkingTogetherPost> walkingTogetherPosts = walkingTogetherPostRepository.findAllByRecommendRoutePost(recommendRoutePost);
        return WalkingTogetherPostMapper.toGetWalkingTogetherPostResponseDtos(walkingTogetherPosts, petBreed);
    }

    @Transactional
    @Override
    public CreateWalkingTogetherPostResponseDto createWalkingTogetherPost(CreateWalkingTogetherPostDto createWalkingTogetherPostDto, Long profileId) {
        Profile profile = profileQueryUseCase.findOrThrow(profileId);
        RecommendRoutePost recommendRoutePost = postQueryUseCase.findOrThrow(createWalkingTogetherPostDto.getRecommendRoutePostId());
        WalkingTogetherPost walkingTogetherPost = WalkingTogetherPostMapper.toEntity(profile, recommendRoutePost, createWalkingTogetherPostDto);
        walkingTogetherPost.matchingStart(profileId, profile);
        WalkingTogetherPost savedWalkingTogetherPost = walkingTogetherPostRepository.save(walkingTogetherPost);
        return new CreateWalkingTogetherPostResponseDto(savedWalkingTogetherPost.getId());
    }

    @Transactional
    @Override
    public void updateWalkingTogetherPost(Long walkingTogetherPostId, UpdateWalkingTogetherPostDto updateWalkingTogetherPostDto, Long profileId) {
        WalkingTogetherPost walkingTogetherPost = walkingTogetherPostQueryUseCase.findOrThrow(walkingTogetherPostId);
        walkingTogetherPost.validated(profileId);
        walkingTogetherPost.update(updateWalkingTogetherPostDto);
    }

    @Transactional
    @Override
    public void deleteWalkingTogetherPost(Long walkingTogetherPostId, Long profileId) {
        WalkingTogetherPost walkingTogetherPost = walkingTogetherPostQueryUseCase.findOrThrow(walkingTogetherPostId);
        walkingTogetherPost.validated(profileId);
        walkingTogetherPostRepository.delete(walkingTogetherPost);
    }


    @Transactional
    @Override
    public CreateChatRoomResponseDto startMatch(Long walkingTogetherPostId, Long profileId) {
        Profile profile = profileQueryUseCase.findOrThrow(profileId);
        WalkingTogetherPost walkingTogetherPost = walkingTogetherPostQueryUseCase.findOrThrow(walkingTogetherPostId);
        PetBreed petBreed = petBreedQueryUseCase.findOrThrow(profile.getPetBreed().getName());

        walkingTogetherPost.checkInMatch(profileId, petBreed);
        walkingTogetherPost.matchingStart(profileId, profile);

        return chatRoomUseCase.createChatRoom(walkingTogetherPost, profile);
    }
}
