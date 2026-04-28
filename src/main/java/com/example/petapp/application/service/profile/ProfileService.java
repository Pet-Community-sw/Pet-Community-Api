package com.example.petapp.application.service.profile;

import com.example.petapp.application.in.member.MemberUseCase;
import com.example.petapp.application.in.petbreed.PetBreedUseCase;
import com.example.petapp.application.in.profile.ProfileUseCase;
import com.example.petapp.application.in.profile.dto.request.ProfileDto;
import com.example.petapp.application.in.profile.dto.response.AccessTokenByProfileIdResponseDto;
import com.example.petapp.application.in.profile.dto.response.CreateProfileResponseDto;
import com.example.petapp.application.in.profile.dto.response.GetProfileResponseDto;
import com.example.petapp.application.in.profile.dto.response.ProfileListResponseDto;
import com.example.petapp.application.in.profile.mapper.ProfileMapper;
import com.example.petapp.application.in.token.TokenUseCase;
import com.example.petapp.application.out.StoragePort;
import com.example.petapp.domain.file.FileKind;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.petbreed.model.PetBreed;
import com.example.petapp.domain.profile.ProfileRepository;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.interfaces.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService implements ProfileUseCase {

    private final PetBreedUseCase petBreedUseCase;
    private final MemberUseCase memberUseCase;
    private final ProfileRepository profileRepository;
    private final TokenUseCase tokenUseCase;
    private final StoragePort storagePort;

    @Transactional//accesstoken 수정 필요 이름이 같은지 확인해야됨.
    @Override
    public CreateProfileResponseDto createProfile(ProfileDto profileDto, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        member.checkProfileCount();
        PetBreed petBreed = petBreedUseCase.findOrThrow(profileDto.getPetBreedId());

        String imageFileName = storagePort.uploadFile(profileDto.getPetImageUrl(), FileKind.PROFILE);
        Profile profile = ProfileMapper.toEntity(profileDto, member, imageFileName, petBreed);
        validateBreed(profileDto, profile);
        profileRepository.save(profile);

        return new CreateProfileResponseDto(profile.getId());
    }


    @Transactional(readOnly = true)
    @Override
    public List<ProfileListResponseDto> getProfiles(Long id) {
        Member member = memberUseCase.findOrThrow(id);
        List<Profile> profiles = profileRepository.findList(member);
        return profiles.stream()
                .map(ProfileMapper::toProfileListResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public GetProfileResponseDto getProfile(Long profileId, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        Profile profile = findOrThrow(profileId);
        return ProfileMapper.toGetProfileResponseDto(profile, member);
    }


    @Transactional
    @Override
    public void updateProfile(Long profileId, ProfileDto profileDto, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        Profile profile = findOrThrow(profileId);
        PetBreed petBreed = petBreedUseCase.findOrThrow(profileDto.getPetBreedId());

        member.validateProfile(member, profile.getMember());
        validateBreed(profileDto, profile);
        String imageFileName = storagePort.uploadFile(profileDto.getPetImageUrl(), FileKind.PROFILE);
        profile.updateProfile(profile, profileDto, imageFileName, petBreed);
    }

    @Transactional
    @Override
    public void deleteProfile(Long profileId, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        Profile profile = findOrThrow(profileId);
        member.validateProfile(member, profile.getMember());
        profileRepository.delete(profile);
    }

    @Transactional
    @Override
    public AccessTokenByProfileIdResponseDto accessTokenByProfile(String accessToken, Long profileId, Long id) {//요청했을 당시 토큰을 redis에 저장시켜서 이전 토큰으로 요청 시 인증이 안되게 끔 해야됨.
        Member member = memberUseCase.findOrThrow(id);
        Profile profile = findOrThrow(profileId);
        member.validateProfile(member, profile.getMember());
        String newAccessToken = tokenUseCase.newAccessTokenByProfile(accessToken, member, profileId);

        return ProfileMapper.toAccessTokenToProfileIdResponseDto(profileId, newAccessToken);
    }

    private void validateBreed(ProfileDto profileDto, Profile profile) {
        List<Long> avoidBreeds = profileDto.getAvoidBreeds();
        for (Long petBreedId : avoidBreeds) {
            PetBreed avoidBreed = petBreedUseCase.findOrThrow(petBreedId);
            profile.addAvoidBreeds(avoidBreed);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Profile findOrThrow(Long id) {
        return profileRepository.find(id).orElseThrow(() -> new ForbiddenException("프로필을 등록해주세요."));
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Profile> find(Long id) {
        return profileRepository.find(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Map<Long, Profile> findMapOrThrow(Set<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }

        Map<Long, Profile> profileMap = profileRepository.findAllByIds(ids).stream()
                .collect(Collectors.toMap(Profile::getId, Function.identity()));

        if (profileMap.size() != ids.size()) {
            throw new ForbiddenException("프로필을 등록해주세요.");
        }

        return profileMap;
    }
}
