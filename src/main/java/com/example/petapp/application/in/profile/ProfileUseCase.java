package com.example.petapp.application.in.profile;

import com.example.petapp.application.in.profile.dto.request.ProfileDto;
import com.example.petapp.application.in.profile.dto.response.AccessTokenByProfileIdResponseDto;
import com.example.petapp.application.in.profile.dto.response.CreateProfileResponseDto;
import com.example.petapp.application.in.profile.dto.response.GetProfileResponseDto;
import com.example.petapp.application.in.profile.dto.response.ProfileListResponseDto;

import java.util.List;

public interface ProfileUseCase {
    CreateProfileResponseDto createProfile(ProfileDto addProfileDto, String email);

    List<ProfileListResponseDto> getProfiles(String email);

    GetProfileResponseDto getProfile(Long profileId, String email);

    void updateProfile(Long profileId, ProfileDto addProfileDto, String email);

    void deleteProfile(Long profileId, String email);

    AccessTokenByProfileIdResponseDto accessTokenByProfile(String accessToken, Long profileId, String email);
}
