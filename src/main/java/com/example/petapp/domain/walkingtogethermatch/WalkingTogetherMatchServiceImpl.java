package com.example.petapp.domain.walkingtogethermatch;


import com.example.petapp.application.in.post.PostQueryUseCase;
import com.example.petapp.application.in.profile.ProfileQueryUseCase;
import com.example.petapp.domain.groupchatroom.ChatRoomService;
import com.example.petapp.domain.groupchatroom.model.dto.response.CreateChatRoomResponseDto;
import com.example.petapp.domain.petbreed.model.entity.PetBreed;
import com.example.petapp.domain.post.recommend.model.entity.RecommendRoutePost;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.query.QueryService;
import com.example.petapp.domain.walkingtogethermatch.mapper.WalkingTogetherMatchMapper;
import com.example.petapp.domain.walkingtogethermatch.model.dto.request.CreateWalkingTogetherMatchDto;
import com.example.petapp.domain.walkingtogethermatch.model.dto.request.UpdateWalkingTogetherMatchDto;
import com.example.petapp.domain.walkingtogethermatch.model.dto.response.CreateWalkingTogetherMatchResponseDto;
import com.example.petapp.domain.walkingtogethermatch.model.dto.response.GetWalkingTogetherMatchResponseDto;
import com.example.petapp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalkingTogetherMatchServiceImpl implements WalkingTogetherMatchService {

    private final ChatRoomService chatRoomService;
    private final WalkingTogetherMatchRepository walkingTogetherMatchRepository;
    private final QueryService queryService;
    private final ProfileQueryUseCase profileQueryUseCase;
    private final PostQueryUseCase<RecommendRoutePost> postQueryUseCase;

    @Transactional(readOnly = true)
    @Override//피해야하는종을 여기서 필터링 하면될듯 피해야하는종에 자신의 종이 포함되어있으면 true를 반환
    public GetWalkingTogetherMatchResponseDto getWalkingTogetherPost(Long walkingTogetherPostId, Long profileId) {
        Profile profile = profileQueryUseCase.findOrThrow(profileId);
        WalkingTogetherMatch walkingTogetherMatch = queryService.findByWalkingTogetherPost(walkingTogetherPostId);
        PetBreed petBreed = queryService.findByPetBreed(profile.getPetBreed().getName());

        return WalkingTogetherMatchMapper.toGetWalkingTogetherPostResponseDto(walkingTogetherPostId, walkingTogetherMatch, profile, petBreed);

    }

    @Transactional(readOnly = true)
    @Override
    public List<GetWalkingTogetherMatchResponseDto> getWalkingTogetherPosts(Long recommendRoutePostId, Long profileId) {
        Profile profile = profileQueryUseCase.findOrThrow(profileId);
        RecommendRoutePost recommendRoutePost = postQueryUseCase.findOrThrow(recommendRoutePostId);
        PetBreed petBreed = queryService.findByPetBreed(profile.getPetBreed().getName());
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
        Profile profile = profileQueryUseCase.findOrThrow(profileId);
        WalkingTogetherMatch walkingTogetherMatch = queryService.findByWalkingTogetherPost(walkingTogetherPostId);
        PetBreed petBreed = queryService.findByPetBreed(profile.getPetBreed().getName());

        walkingTogetherMatch.checkInMatch(profileId, petBreed);
        walkingTogetherMatch.matchingStart(profileId, profile);

        return chatRoomService.createChatRoom(walkingTogetherMatch, profile);
    }
}
