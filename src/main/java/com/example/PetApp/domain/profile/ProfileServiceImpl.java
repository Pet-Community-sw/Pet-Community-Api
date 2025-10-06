package com.example.PetApp.domain.profile;

import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.petbreed.model.entity.PetBreed;
import com.example.PetApp.domain.profile.model.dto.request.ProfileDto;
import com.example.PetApp.domain.profile.model.dto.response.AccessTokenByProfileIdResponseDto;
import com.example.PetApp.domain.profile.model.dto.response.CreateProfileResponseDto;
import com.example.PetApp.domain.profile.model.dto.response.GetProfileResponseDto;
import com.example.PetApp.domain.profile.model.dto.response.ProfileListResponseDto;
import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.common.exception.ConflictException;
import com.example.PetApp.domain.profile.mapper.ProfileMapper;
import com.example.PetApp.domain.query.QueryService;
import com.example.PetApp.domain.token.TokenService;
import com.example.PetApp.common.base.util.imagefile.FileImageKind;
import com.example.PetApp.common.base.util.imagefile.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    @Value("${spring.dog.profile.image.upload}")
    private String profileUploadDir;

    private final QueryService queryService;
    private final ProfileRepository profileRepository;
    private final TokenService tokenService;

    @Transactional//accesstoken 수정 필요 이름이 같은지 확인해야됨.
    @Override
    public CreateProfileResponseDto createProfile(ProfileDto profileDto, String email) {
        Member member = queryService.findByMember(email);
        if (profileRepository.countByMember(member) >= 4) {
            throw new ConflictException("프로필은 최대 4개 입니다.");
        }
        PetBreed petBreed = queryService.findByPetBreed(profileDto.getPetBreedId());

        String imageFileName = FileUploadUtil.fileUpload(profileDto.getPetImageUrl(), profileUploadDir, FileImageKind.PROFILE);
        Profile profile = ProfileMapper.toEntity(profileDto, member, imageFileName, petBreed);
        validateBreed(profileDto, profile);
        profileRepository.save(profile);

        return new CreateProfileResponseDto(profile.getId());
    }


    @Transactional(readOnly = true)
    @Override
    public List<ProfileListResponseDto> getProfiles(String email) {
        Member member = queryService.findByMember(email);
        List<Profile> profiles = profileRepository.findByMember(member);
        return profiles.stream()
                .map(ProfileMapper::toProfileListResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public GetProfileResponseDto getProfile(Long profileId, String email) {
        Member member = queryService.findByMember(email);
        Profile profile = queryService.findByProfile(profileId);
        return ProfileMapper.toGetProfileResponseDto(profile, member);
    }


    @Transactional
    @Override
    public void updateProfile(Long profileId, ProfileDto profileDto, String email) {
        Member member = queryService.findByMember(email);
        Profile profile = queryService.findByProfile(profileId);
        PetBreed petBreed = queryService.findByPetBreed(profileDto.getPetBreedId());

        member.validateProfile(member, profile.getMember());
        validateBreed(profileDto, profile);
        String imageFimeName = FileUploadUtil.fileUpload(profileDto.getPetImageUrl(), profileUploadDir, FileImageKind.PROFILE);
        profile.updateProfile(profile, profileDto, imageFimeName, petBreed);
    }

    @Transactional
    @Override
    public void deleteProfile(Long profileId, String email) {
        Member member = queryService.findByMember(email);
        Profile profile = queryService.findByProfile(profileId);
        member.validateProfile(member, profile.getMember());
        profileRepository.deleteById(profileId);
    }

    @Transactional
    @Override
    public AccessTokenByProfileIdResponseDto accessTokenByProfile(String accessToken, String refreshToken, Long profileId, String email) {//요청했을 당시 토큰을 redis에 저장시켜서 이전 토큰으로 요청 시 인증이 안되게 끔 해야됨.
        Member member = queryService.findByMember(email);
        Profile profile = queryService.findByProfile(profileId);
        member.validateProfile(member, profile.getMember());
        String newAccessToken = tokenService.newAccessTokenByProfile(accessToken, refreshToken, member, profileId);

        return ProfileMapper.toAccessTokenToProfileIdResponseDto(profileId, newAccessToken);
    }

    private void validateBreed(ProfileDto profileDto, Profile profile) {
        List<Long> avoidBreeds = profileDto.getAvoidBreeds();
        for (Long petBreedId : avoidBreeds) {
            PetBreed avoidBreed = queryService.findByPetBreed(petBreedId);
            profile.addAvoidBreeds(avoidBreed);
        }
    }
}
