package com.example.petapp.domain.profile;

import com.example.petapp.domain.profile.model.dto.request.ProfileDto;
import com.example.petapp.domain.profile.model.dto.response.AccessTokenByProfileIdResponseDto;
import com.example.petapp.domain.profile.model.dto.response.CreateProfileResponseDto;
import com.example.petapp.domain.profile.model.dto.response.GetProfileResponseDto;
import com.example.petapp.domain.profile.model.dto.response.ProfileListResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProfileService {
    CreateProfileResponseDto createProfile(ProfileDto addProfileDto, String email);

    List<ProfileListResponseDto> getProfiles(String email);

    GetProfileResponseDto getProfile(Long profileId, String email);

    void updateProfile(Long profileId, ProfileDto addProfileDto, String email);

    void deleteProfile(Long profileId, String email);

    AccessTokenByProfileIdResponseDto accessTokenByProfile(String accessToken, Long profileId, String email);
}
