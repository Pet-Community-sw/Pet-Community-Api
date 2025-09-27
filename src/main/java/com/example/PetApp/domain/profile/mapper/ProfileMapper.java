package com.example.PetApp.domain.profile.mapper;

import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.petbreed.model.entity.PetBreed;
import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.domain.profile.model.dto.response.AccessTokenByProfileIdResponseDto;
import com.example.PetApp.domain.profile.model.dto.response.GetProfileResponseDto;
import com.example.PetApp.domain.profile.model.dto.request.ProfileDto;
import com.example.PetApp.domain.profile.model.dto.response.ProfileListResponseDto;
import com.example.PetApp.common.util.AgeUtil;

import java.time.LocalDate;
import java.time.MonthDay;

import static com.example.PetApp.common.util.AgeUtil.CalculateAge;

public class ProfileMapper {
    public static Profile toEntity(ProfileDto profileDto, Member member, String imageFileName, PetBreed petBreed) {
        return Profile.builder()
                .member(member)
                .petImageUrl(imageFileName)
                .petBirthDate(profileDto.getPetBirthDate())
                .extraInfo(profileDto.getExtraInfo())
                .petBreed(petBreed)
                .petAge(AgeUtil.CalculateAge(profileDto.getPetBirthDate())+"살")
                .petName(profileDto.getPetName())
                .build();
    }

    public static GetProfileResponseDto toGetProfileResponseDto(Profile profile, Member member) {
        return GetProfileResponseDto.builder()
                .profileId(profile.getId())
                .petBreed(String.valueOf(profile.getPetBreed()))
                .petImageUrl(profile.getPetImageUrl())
                .memberId(profile.getMember().getId())
                .petName(profile.getPetName())
                .petAge(profile.getPetAge())
                .petBirthDate(profile.getPetBirthDate())
                .avoidBreeds(profile.getAvoidBreeds())
                .isOwner(member.equals(profile.getMember()))
                .build();
    }

    public static ProfileListResponseDto toProfileListResponseDto(Profile profile) {
        boolean isBirthday = MonthDay.from(LocalDate.now())
                .equals(MonthDay.from(profile.getPetBirthDate()));
        return ProfileListResponseDto.builder()
                .profileId(profile.getId())
                .petImageUrl(profile.getPetImageUrl())
                .petName(profile.getPetName())
                .hasBirthday(isBirthday)
                .build();
    }

    public static AccessTokenByProfileIdResponseDto toAccessTokenToProfileIdResponseDto(Long profileId, String accessToken) {
        return AccessTokenByProfileIdResponseDto.builder()
                .profileId(profileId)
                .accessToken(accessToken)
                .build();
    }

}
