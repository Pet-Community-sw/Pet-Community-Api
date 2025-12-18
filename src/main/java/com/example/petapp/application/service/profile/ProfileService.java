package com.example.petapp.application.service.profile;

import com.example.petapp.application.common.imagefile.FileImageKind;
import com.example.petapp.application.common.imagefile.FileUploadUtil;
import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.application.in.petbreed.PetBreedQueryUseCase;
import com.example.petapp.application.in.profile.ProfileQueryUseCase;
import com.example.petapp.application.in.profile.ProfileUseCase;
import com.example.petapp.application.in.profile.dto.request.ProfileDto;
import com.example.petapp.application.in.profile.dto.response.AccessTokenByProfileIdResponseDto;
import com.example.petapp.application.in.profile.dto.response.CreateProfileResponseDto;
import com.example.petapp.application.in.profile.dto.response.GetProfileResponseDto;
import com.example.petapp.application.in.profile.dto.response.ProfileListResponseDto;
import com.example.petapp.application.in.profile.mapper.ProfileMapper;
import com.example.petapp.application.in.token.TokenUseCase;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.petbreed.model.PetBreed;
import com.example.petapp.domain.profile.ProfileRepository;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.interfaces.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService implements ProfileUseCase {

    private final PetBreedQueryUseCase petBreedQueryUseCase;
    private final MemberQueryUseCase memberQueryUseCase;
    private final ProfileRepository profileRepository;
    private final TokenUseCase tokenUseCase;
    private final ProfileQueryUseCase profileQueryUseCase;
    @Value("${spring.dog.profile.image.upload}")
    private String profileUploadDir;

    @Transactional//accesstoken 수정 필요 이름이 같은지 확인해야됨.
    @Override
    public CreateProfileResponseDto createProfile(ProfileDto profileDto, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        if (profileRepository.count(member) >= 4) {
            throw new ConflictException("프로필은 최대 4개 입니다.");
        }
        PetBreed petBreed = petBreedQueryUseCase.findOrThrow(profileDto.getPetBreedId());

        String imageFileName = FileUploadUtil.fileUpload(profileDto.getPetImageUrl(), profileUploadDir, FileImageKind.PROFILE);
        Profile profile = ProfileMapper.toEntity(profileDto, member, imageFileName, petBreed);
        validateBreed(profileDto, profile);
        profileRepository.save(profile);

        return new CreateProfileResponseDto(profile.getId());
    }


    @Transactional(readOnly = true)
    @Override
    public List<ProfileListResponseDto> getProfiles(String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        List<Profile> profiles = profileRepository.findList(member);
        return profiles.stream()
                .map(ProfileMapper::toProfileListResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public GetProfileResponseDto getProfile(Long profileId, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        Profile profile = profileQueryUseCase.findOrThrow(profileId);
        return ProfileMapper.toGetProfileResponseDto(profile, member);
    }


    @Transactional
    @Override
    public void updateProfile(Long profileId, ProfileDto profileDto, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        Profile profile = profileQueryUseCase.findOrThrow(profileId);
        PetBreed petBreed = petBreedQueryUseCase.findOrThrow(profileDto.getPetBreedId());

        member.validateProfile(member, profile.getMember());
        validateBreed(profileDto, profile);
        String imageFimeName = FileUploadUtil.fileUpload(profileDto.getPetImageUrl(), profileUploadDir, FileImageKind.PROFILE);
        profile.updateProfile(profile, profileDto, imageFimeName, petBreed);
    }

    @Transactional
    @Override
    public void deleteProfile(Long profileId, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        Profile profile = profileQueryUseCase.findOrThrow(profileId);
        member.validateProfile(member, profile.getMember());
        profileRepository.delete(profile);
    }

    @Transactional
    @Override
    public AccessTokenByProfileIdResponseDto accessTokenByProfile(String accessToken, Long profileId, String email) {//요청했을 당시 토큰을 redis에 저장시켜서 이전 토큰으로 요청 시 인증이 안되게 끔 해야됨.
        Member member = memberQueryUseCase.findOrThrow(email);
        Profile profile = profileQueryUseCase.findOrThrow(profileId);
        member.validateProfile(member, profile.getMember());
        String newAccessToken = tokenUseCase.newAccessTokenByProfile(accessToken, member, profileId);

        return ProfileMapper.toAccessTokenToProfileIdResponseDto(profileId, newAccessToken);
    }

    private void validateBreed(ProfileDto profileDto, Profile profile) {
        List<Long> avoidBreeds = profileDto.getAvoidBreeds();
        for (Long petBreedId : avoidBreeds) {
            PetBreed avoidBreed = petBreedQueryUseCase.findOrThrow(petBreedId);
            profile.addAvoidBreeds(avoidBreed);
        }
    }
}
