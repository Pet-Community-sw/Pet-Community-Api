package com.example.petapp.application.in.profile;

import com.example.petapp.application.in.profile.dto.request.ProfileDto;
import com.example.petapp.application.in.profile.dto.response.AccessTokenByProfileIdResponseDto;
import com.example.petapp.application.in.profile.dto.response.CreateProfileResponseDto;
import com.example.petapp.application.in.profile.dto.response.GetProfileResponseDto;
import com.example.petapp.application.in.profile.dto.response.ProfileListResponseDto;

import java.util.List;

public interface ProfileUseCase {
    CreateProfileResponseDto createProfile(ProfileDto addProfileDto, Long id);

    List<ProfileListResponseDto> getProfiles(Long id);

    GetProfileResponseDto getProfile(Long profileId, Long id);

    void updateProfile(Long profileId, ProfileDto addProfileDto, Long id);

    void deleteProfile(Long profileId, Long id);

    AccessTokenByProfileIdResponseDto accessTokenByProfile(String accessToken, Long profileId, Long id);
}
